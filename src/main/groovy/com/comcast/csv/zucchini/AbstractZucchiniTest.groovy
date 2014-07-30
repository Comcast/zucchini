package com.comcast.csv.zucchini;

import java.util.concurrent.atomic.AtomicInteger

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.testng.Assert;
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test

import cucumber.api.testng.TestNGCucumberRunner


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
abstract class AbstractZucchiniTest {

    private static Logger logger = LoggerFactory.getLogger(AbstractZucchiniTest.class)
    
    @Test
    public void run() {
        List<TestContext> contexts = getTestContexts()
        isParallel() ? runParallel(contexts) : runSerial(contexts)
    }
    
    void runParallel(List<TestContext> contexts) {
        List<Thread> threads = []
        int failures = 0
        
        contexts.each { TestContext context ->
            threads.push(Thread.start {
                failures += runWith(context) ? 0 : 1
            })
        }
        
        threads.each { it.join() }
        
        Assert.assertEquals(failures, 0, "There were ${failures} executions against a TestContext")
    }
    
    void runSerial(List<TestContext> contexts) {
        int failures = 0

        contexts.each {
            failures += runWith(it) ? 0 : 1
        }

        Assert.assertEquals(failures, 0, "There were ${failures} executions against a TestContext")
    }
    
    /**
     * Run all configured cucumber features and scenarios against the given TestContext.
     * 
     * @param context the test context
     * @return true if successful, otherwise false
     */
    boolean runWith(TestContext context) {
        try {
            TestContext.setCurrent(context)
    
            logger.debug("ZucchiniTest[${context.name}] starting")
            new TestNGCucumberRunner(getClass()).runCukes();
            logger.debug("ZucchiniTest[${context.name}] finished")
            
            cleanup(context)
            TestContext.removeCurrent()
            return true;
        } catch (Throwable t) {
            t.printStackTrace()
            return false
        }
    }

    /**
     * If this returns true, all cucumber features will run against each TestContext in parallel, otherwise
     * they will run one after the other (in order). Override this method to change the output.
     * 
     * <b>The default value is <code>true</code> so the default behavior is parallel execution.</b>
     */
    public boolean isParallel() {
        return true
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
        logger.debug("Cleanup method was not implemented for ${out}")
    }
}
