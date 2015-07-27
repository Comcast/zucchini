package com.comcast.csv.zucchini;

import java.util.ArrayList;
import java.util.List;
import java.util.AbstractMap;
import java.util.Hashtable;

import org.json.JSONObject;
import org.json.JSONArray;

import net.masterthought.cucumber.ReportBuilder;

import org.apache.commons.lang.mutable.MutableInt;
import org.apache.maven.plugin.MojoExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import gherkin.formatter.Formatter;


/**
 * Constructs a suite of Cucumber tests for every TestContext as returned by the
 * {@link #getTestContexts()} method. This should be used when working with either external
 * hardware or a virtual device (like a browser) to run the same cucumber tests but against
 * a different test target.
 *
 * To do this correctly, each step ("given", "when" or "then") should get access to the object
 * under test by calling {@link TestContext.getCurrent()}.
 *
 * @author Clark Malmgren
 */
public abstract class AbstractZucchiniTest {

    private static Logger logger = LoggerFactory.getLogger(AbstractZucchiniTest.class);
    private TestNGZucchiniRunner runner;
    public static Hashtable<String, JSONArray> featureSet = new Hashtable<String, JSONArray>();

    /* Synchronization and global variables.  DO NOT TOUCH! */
    private static Boolean hooked = false;

    private void genHook() {
        synchronized(hooked) {
            /* prevent this from being added multiple times */
            if(hooked) return;
            hooked = true;
        }

        /* add a shutdown hook, as this will allow all Zucchini tests to complete without
         * knowledge of each other's existence */
        Runtime.getRuntime().addShutdownHook(new ZucchiniShutdownHook());
    }

    @AfterClass
    public void generateReports() {
        //this does nothing now, left for API consistency
    }

    @Test
    public void run() {
        List<TestContext> contexts = this.getTestContexts();

        String zucchiniSerialize = System.getenv().get("ZUCCHINI_SERIALIZE");

        if(zucchiniSerialize == null)
            zucchiniSerialize = "no";

        zucchiniSerialize = zucchiniSerialize.toLowerCase();

        boolean env_serialize = (zucchiniSerialize.equals("yes")) || (zucchiniSerialize.equals("y")) || (zucchiniSerialize.equals("true")) || (zucchiniSerialize.equals("1"));

        if(env_serialize) {
            this.runSerial(contexts);
        }
        else {
            if(this.isParallel())
                this.runParallel(contexts);
            else
                this.runSerial(contexts);
        }
    }

    public void runParallel(List<TestContext> contexts) {
        List<Thread> threads = new ArrayList<Thread>(contexts.size());
        int failures = 0;

        MutableInt mi = new MutableInt();

        for(TestContext tc : contexts) {
            Thread t = new Thread(new TestRunner(this, tc, mi), tc.name);
            threads.add(t);
            t.start();
            //threads.push(Thread.start(new TestRunner(this, tc, mi), tc.name));
        }

        for(Thread t : threads) {
            try {
                t.join();
            }
            catch(Throwable e) {
                logger.error(t.toString());
            }
        }

        Assert.assertEquals(mi.intValue() , 0, String.format("There were %d executions against a TestContext", failures));
    }

    public void runTest(TestContext tc, MutableInt mi) {
        if(!this.runWith(tc))
            mi.increment();
    }

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

        logger.debug(String.format("ZucchiniTest[%s] starting", context.name));
        TestNGZucchiniRunner runner = new TestNGZucchiniRunner(getClass());

        try {
            setup(context);
            setupFormatter(context, runner);
            runner.runCukes();
            return true;
        } catch (Throwable t) {
            t.printStackTrace();
            return false;
        } finally {
            logger.debug(String.format("ZucchiniTest[%s] finished", context.name));

            ZucchiniOutput options = this.getClass().getAnnotation(ZucchiniOutput.class);
            String fileName;

            if(options!= null)
                fileName = options.json();
            else
                fileName = "target/zucchini.json";

            JSONArray results = new JSONArray(runner.getJSONOutput());

            synchronized(featureSet) {
                JSONArray features = null;

                if(!AbstractZucchiniTest.featureSet.containsKey(fileName))
                    AbstractZucchiniTest.featureSet.put(fileName, new JSONArray());

                features = AbstractZucchiniTest.featureSet.get(fileName);

                JSONObject idxObj;
                String tmp;

                for(int i = 0; i < results.length(); i++) {
                    idxObj = results.getJSONObject(i);

                    if(idxObj != null) {
                        //update id
                        if(idxObj.has("id"))
                            tmp = idxObj.getString("id");
                        else
                            tmp = "";
                        idxObj.put("id", "--zucchini--" + context.name + "-" + tmp);

                        //update uri
                        if(idxObj.has("uri"))
                            tmp = idxObj.getString("uri");
                        else
                            tmp = "";
                        idxObj.put("uri", "--zucchini--" + context.name + "-" + tmp);

                        //update name
                        if(idxObj.has("name"))
                            tmp = idxObj.getString("name");
                        else
                            tmp = "";
                        idxObj.put("name", "ZucchiniTestContext[" + context.name + "]:: " + tmp);

                        features.put(idxObj);
                    }
                }
            }

            cleanup(context);
            TestContext.removeCurrent();
        }
    }

    /**
     * If this returns true, all cucumber features will run against each TestContext in parallel, otherwise
     * they will run one after the other (in order). Override this method to change the output.
     *
     * <b>The default value is <code>true</code> so the default behavior is parallel execution.</b>
     */
    public boolean isParallel() {
        return true;
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
        logger.debug("Cleanup method was not implemented for " + this.getClass().getSimpleName());
    }

    /**
     * Optionally override this method to do custom setup for the object under test
     *
     * @param out the object under test to setup
     **/
    public void setup(TestContext out) {
        logger.debug("Setup method was not implemented for " + this.getClass().getSimpleName());
    }

    /**
     * Configures formatter(s) of your choosing or overrides their default behavior
     * @param out The object under test
     * @param runner The object used for test execution
     * To modify formatters, a sample such as the one below should be used:
     * <pre>
     * List<Formatter> formatters = runner.getFormatters();
     * for (Formatter formatter : formatters) {
     *   if (formatter instanceof YourCustomFormatter) {
     *     ((YourCustomFormatter)formatter).yourFormatterModifierMethod();
     *   }
     * }
     *
     * </pre>
     */
    public void setupFormatter(TestContext out, TestNGZucchiniRunner runner) {
        logger.debug("Setup formatter method was not implemented for " + this.getClass().getSimpleName());
    }
}
