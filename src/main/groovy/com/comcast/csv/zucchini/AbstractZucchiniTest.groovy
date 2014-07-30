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

    protected List<TestContext> contexts

    /**
     * Internal method that gathers all objects under test so that they will be available during 
     * the test runs. DO NOT OVERLOAD THIS METHOD.
     */
    @BeforeClass
    public void intakeTestObjects() {
        this.contexts = getTestContexts()
    }
    
    /**
     * Provides some protection to ensure that actual tests extend AbstractParallelZucchiniTest or AbstractSerialZucchiniTest
     * instead of this directly. 
     */
    @BeforeClass
    void verifyProperInheritence() {
        Assert.fail("Do not extend AbstractZucchiniTest directly, instead extend AbstractParallelZucchiniTest or AbstractSerialZucchiniTest")
    } 
    
    /**
     * Run all configured cucumber features and scenarios against the given TestContext.
     * 
     * @param context the test context
     */
    protected void run(TestContext context) {
        TestContext.setCurrent(context)

        logger.debug("ZucchiniTest[${context.name}] starting")
        new TestNGCucumberRunner(getClass()).runCukes();
        logger.debug("ZucchiniTest[${context.name}] finished")
        
        cleanup(context)
        TestContext.removeCurrent()
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
    void cleanup(TestContext out) {
        logger.debug("Cleanup method was not implemented for ${out}")
    }
}
