package com.comcast.zucchini;

/**
 * This creates a barrier sync when using the Zucchini framework.
 *
 * This must be active under an AbstractZucchiniTest.
 *
 * @author Andrew Benton
 */
public class Barrier {

    protected Barrier() {
    }

    /**
     * Creates a barrier sync that will wait until all {@see TestContext}'s join or fail.
     *
     * @return The order in which the runners are released from the barrier.
     */
    public static int sync() {
        return Barrier.sync(-1);
    }

    /**
     * Creates a barrier sync that will wait until `milliseconds` after the first TestContext hits the barrier or all {@see TestContext}'s join or fail.
     *
     * @param milliseconds The amount of time allocated to wait until the barrier times out and halts non-waiting threads.
     * @return The order in which the runners are released from the barrier.
     */
    public static int sync(int milliseconds) {
        TestContext tc = TestContext.getCurrent();

        if(tc == null)
            return -1;

        AbstractZucchiniTest azt = tc.getParentTest();

        if(azt.isParallel())
            return azt.flexBarrier.await(milliseconds);
        else
            return 0;
    }
}
