package com.comcast.csv.zucchini;

import java.util.concurrent.atomic.AtomicInteger

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test

import cucumber.api.testng.TestNGCucumberRunner


/**
 * Constructs a suite of Cucumber tests for every object under test as returned by the
 * {@link #getObjectsToTest()} method. This should be used when working with either external
 * hardware or a virtual device (like a browser) to run the same cucumber tests but against
 * a different test target.
 * 
 * To do this correctly, each step ("given", "when" or "then") should get access to the object
 * under test by calling {@link ObjectUnderTest.get()}.
 * 
 * Note: This can only handle up to 64 max targets to test.
 * 
 * @author Clark Malmgren
 *
 * @param <T> the test object type
 */
abstract class AbstractZucchiniTest<T> {

    private static Logger logger = LoggerFactory.getLogger(AbstractZucchiniTest.class)

    private AtomicInteger count = new AtomicInteger(0)

    private List<T> outs

    /**
     * Internal method that gathers all objects under test so that they will be available during 
     * the test runs. DO NOT OVERLOAD THIS METHOD.
     */
    @BeforeClass
    public void intakeTestObjects() {
        this.outs = getObjectsToTest();
    }

    /**
     * Actually runs the test in parallel.
     * 
     * @throws IOException if it breaks, don't question it
     */
    @Test(threadPoolSize = 64, invocationCount = 64)
    public void run() throws IOException {
        int index = count.getAndIncrement();
        if (index < outs.size()) {
            T out = outs.get(index);
            ObjectUnderTest.set(out);

            logger.debug("ParallelCucumberTest[${index}] for ObjectUnderTest[${out}] starting")
            new TestNGCucumberRunner(getClass()).runCukes();
            logger.debug("ParallelCucumberTest[${index}] for ObjectUnderTest[${out}] finished")
            cleanup(out)
        }
    }

    /**
     * Returns the full list of objects to test against. The full suite of cucumber features
     * and scenarios will be run against these object in parallel.
     * 
     * @return the full list of objects to test against.
     */
    public abstract List<T> getObjectsToTest();

    /**
     * Optionally override this method to do custom cleanup for the object under test
     * 
     * @param out the object under test to cleanup
     */
    void cleanup(T out) {
        logger.debug("Cleanup method was not implemented for ${out}")
    }
}
