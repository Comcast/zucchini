package com.comcast.zucchini;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.List;
import java.util.Iterator;
import java.util.Collections;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.mutable.MutableInt;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Constructs a suite of Cucumber tests for every TestContext as returned by the
 * {@link AbstractZucchiniTest#getTestContexts()} method. This should be used when working with either external
 * hardware or a virtual device (like a browser) to run the same cucumber tests but against
 * a different test target.
 *
 * To do this correctly, each step ("given", "when" or "then") should get access to the object
 * under test by calling {@link TestContext#getCurrent()}.
 *
 * @author Clark Malmgren
 */
public abstract class AbstractZucchiniTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractZucchiniTest.class);
    static Map<String, JsonArray> featureSet = new HashMap<String, JsonArray>();

    /* Synchronization and global variables.  DO NOT TOUCH! */
    private static Object lock = new Object();
    private static Boolean hooked = false;

    /* store the list of contexts here */
    List<TestContext> contexts;
    /* List the contexts that have failed here */
    Set<TestContext> failedContexts;

    /* pre-scenario cdl's */
    StaticBarrier phase0;
    StaticBarrier phase1;

    /* flexible barrier for test-wise locking */
    FlexibleBarrier flexBarrier;

    private void genHook() {
        if(hooked)
            return;

        synchronized(lock) {
            /* prevent this from being added multiple times */
            if(hooked)
                return;

            hooked = true;
        }

        /* add a shutdown hook, as this will allow all Zucchini tests to complete without
         * knowledge of each other's existence */
        Runtime.getRuntime().addShutdownHook(new ZucchiniShutdownHook());
    }

    /**
     * This is a hook used to generate reports, if needed.
     *
     * By default, this is unused, as it was replaced by the {@link ZucchiniShutdownHook}.
     */
    @AfterClass
    public void generateReports() {
        //this does nothing now, left for API consistency
    }

    /**
     * The TestNG hook that is used to startup the Zucchini/Cucumber tests.
     *
     * This configures the various state variables needed for a successful run, and then makes a call about serialized or parallelized runs before beginning.
     */
    @Test
    public void run() {
        this.contexts = this.getTestContexts();
        this.failedContexts = Collections.newSetFromMap(new ConcurrentHashMap<TestContext, Boolean>());

        LOGGER.trace("Creating AbstractZucchiniTest with contexts: {}", this.contexts);

        this.phase0 = new StaticBarrier(this.contexts.size());
        this.phase1 = new StaticBarrier(this.contexts.size());

        this.flexBarrier = new FlexibleBarrier(this);

        for(TestContext tc : this.contexts) {
            tc.parentTest = this;
        }

        if(this.isParallel())
            this.runParallel(contexts);
        else
            this.runSerial(contexts);
    }

    private boolean envSerialized() {
        String zucchiniSerialize = System.getenv().get("ZUCCHINI_SERIALIZE");

        if(zucchiniSerialize == null)
            zucchiniSerialize = "no";

        zucchiniSerialize = zucchiniSerialize.toLowerCase();

        return ("yes" ).equals(zucchiniSerialize) ||
               ("y"   ).equals(zucchiniSerialize) ||
               ("true").equals(zucchiniSerialize) ||
               ("1"   ).equals(zucchiniSerialize);
    }

    /**
     * Run the parallel implementation of the test.
     *
     * This is normally run by the run() method.  Call this at your own risk.
     *
     * @param contexts The list of contexts that will be run.
     */
    public void runParallel(List<TestContext> contexts) {
        List<Thread> threads = new ArrayList<Thread>(contexts.size());

        MutableInt mi = new MutableInt();

        for(TestContext tc : contexts) {
            Thread t = new Thread(new TestRunner(this, tc, mi), tc.name);
            threads.add(t);
            t.start();
        }

        int joinCount = 0;

        for(Thread t : threads) {
            try {
                t.join();
                joinCount++;
            }
            catch(InterruptedException e) {
                LOGGER.error("ERROR on Thread.join(): {}", e);
            }
        }

        Assert.assertEquals(joinCount, contexts.size(), String.format("There were %d contexts launched, but only %d rejoined.", contexts.size(), joinCount));
        Assert.assertEquals(mi.intValue() , 0, String.format("There were %d failed executions against a TestContext", mi.intValue()));
    }

    /**
     * Similar to the runParallel method, this runs the test contexts in a serialized fashion.
     *
     * @param contexts The list of contexts that will be run.
     */
    public void runSerial(List<TestContext> contexts) {
        int failures = 0;

        for(TestContext tc : contexts)
            if(!this.runWith(tc))
                failures += 1;

        Assert.assertEquals(failures, 0, String.format("There were %d executions against a TestContext", failures));
    }

    /**
     * Run all configured cucumber features and scenarios against the given TestContext.
     *
     * @param context the test context
     * @return true if successful, otherwise false
     */
    public boolean runWith(TestContext context) {
        this.genHook();

        TestContext.setCurrent(context);

        LOGGER.debug(String.format("ZucchiniTest[%s] starting", context.name));
        TestNGZucchiniRunner runner = new TestNGZucchiniRunner(getClass());

        boolean ret = false;

        try {
            setup(context);
            setupFormatter(context, runner);
            runner.runCukes();
            ret = true;
        } catch (RuntimeException t) {
            LOGGER.error("ERROR running test: {}", t);
            ret = false;
        } finally {
            LOGGER.debug(String.format("ZucchiniTest[%s] finished", context.name));

            ZucchiniOutput options = this.getClass().getAnnotation(ZucchiniOutput.class);
            String fileName;

            if(options!= null)
                fileName = options.json();
            else
                fileName = "target/zucchini.json";

            JsonParser parser = new JsonParser();
            JsonElement result = parser.parse(runner.getJSONOutput());

            synchronized(featureSet) {
                JsonArray features = null;

                if(!AbstractZucchiniTest.featureSet.containsKey(fileName))
                    AbstractZucchiniTest.featureSet.put(fileName, new JsonArray());

                features = AbstractZucchiniTest.featureSet.get(fileName);

                if(result.isJsonArray()) {
                    JsonArray jarr = (JsonArray)result;
                    JsonElement jel = null;
                    JsonObject jobj = null;

                    Iterator<JsonElement> jels = jarr.iterator();

                    while(jels.hasNext()) {
                        jel = jels.next();

                        if(jel.isJsonObject()) {
                            jobj = (JsonObject)jel;
                            upgradeObject(jobj, context.name);
                            features.add(jobj);
                        }
                        else {
                            features.add(jel);
                        }
                    }
                }
                else if(result.isJsonObject()) {
                    JsonObject jobj = (JsonObject)result;
                    upgradeObject(jobj, context.name);
                    features.add(jobj);
                }
                else {
                    features.add(result);
                }
            }

            cleanup(context);
            TestContext.removeCurrent();
        }

        return ret;
    }

    private void upgradeObject(JsonObject jobj, String ctxName) {
        String tmp;
        if(jobj.has("id"))
            tmp = "--zucchini--" + ctxName + "-" + jobj.get("id").getAsString();
        else
            tmp = "--zucchini--" + ctxName + "-";
        jobj.addProperty("id", tmp);

        if(jobj.has("uri"))
            tmp = "--zucchini--" + ctxName + "-" + jobj.get("uri").getAsString();
        else
            tmp = "--zucchini--" + ctxName + "-";
        jobj.addProperty("uri", tmp);

        if(jobj.has("name"))
            tmp = "ZucchiniTestContext[" + ctxName + "]::" + jobj.get("name").getAsString();
        else
            tmp = "ZucchiniTestContext[" + ctxName + "]::";
        jobj.addProperty("name", tmp);
    }

    /**
     * If this returns true, all cucumber features will run against each TestContext in parallel, otherwise
     * they will run one after the other (in order). Override this method to change the output.
     *
     * <b>The default value is <code>true</code> so the default behavior is parallel execution.</b>
     * @return True if Zucchini is going to run TestContexts in parallel or False if serially
     */
    public boolean isParallel() {
        return !this.envSerialized();
    }

    /**
     * Returns the full list of objects to test against. The full suite of cucumber features
     * and scenarios will be run against these object in parallel.
     *
     * @return the full list of objects to test against.
     */
    public abstract List<TestContext> getTestContexts();

    /**
     * Optionally override this method to do custom cleanup for the object under test
     *
     * @param out the object under test to cleanup
     */
    public void cleanup(TestContext out) {
        LOGGER.debug("Cleanup method was not implemented for " + this.getClass().getSimpleName());
    }

    /**
     * Optionally override this method to do custom setup for the object under test
     *
     * @param out the object under test to setup
     **/
    public void setup(TestContext out) {
        LOGGER.debug("Setup method was not implemented for " + this.getClass().getSimpleName());
    }

    /**
     * Configures formatter(s) of your choosing or overrides their default behavior
     * @param out The object under test
     * @param runner The object used for test execution
     * To modify formatters, a sample such as the one below should be used:
     * <pre>
     * {@code
     * List<Formatter> formatters = runner.getFormatters();
     * for (Formatter formatter : formatters) {
     *   if (formatter instanceof YourCustomFormatter) {
     *     ((YourCustomFormatter)formatter).yourFormatterModifierMethod();
     *   }
     * }
     * }
     * </pre>
     */
    public void setupFormatter(TestContext out, TestNGZucchiniRunner runner) {
        LOGGER.debug("Setup formatter method was not implemented for " + this.getClass().getSimpleName());
    }
}
