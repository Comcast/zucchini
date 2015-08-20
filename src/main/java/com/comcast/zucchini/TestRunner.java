package com.comcast.zucchini;

import org.apache.commons.lang.mutable.MutableInt;

/**
 * Provides the thread-wise context in order to run the parallel Zucchini tests.
 *
 * @author Andrew Benton
 */
public class TestRunner implements Runnable
{
    private AbstractZucchiniTest test;
    private TestContext tc;
    private MutableInt mi;

    public TestRunner(AbstractZucchiniTest test, TestContext tc, MutableInt mi)
    {
        this.test = test;
        this.tc = tc;
        this.mi = mi;
    }

    @Override
    public void run()
    {
        if(!this.test.runWith(this.tc)) {
            synchronized(this.mi) {
                this.mi.increment();
            }
        }
    }
}
