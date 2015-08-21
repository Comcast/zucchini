package com.comcast.zucchini;

import org.apache.commons.lang.mutable.MutableInt;

/**
 * Provides the thread-wise context in order to run the parallel Zucchini tests.
 *
 * @author Andrew Benton
 */
public class TestRunner implements Runnable {

    private AbstractZucchiniTest test;
    private TestContext tc;
    private MutableInt mi;

    /**
     * Create a test runner linked to the test.
     *
     * This should only be used for parallel runs
     *
     * @param test The AbstractZucchiniTest that this will run on top of
     * @param tc The test context that this will be linked to
     * @param mi The running count of the number of failures
     */
    public TestRunner(AbstractZucchiniTest test, TestContext tc, MutableInt mi) {
        this.test = test;
        this.tc = tc;
        this.mi = mi;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        if(!this.test.runWith(this.tc)) {
            synchronized(this.mi) {
                this.mi.increment();
            }
        }
    }
}
