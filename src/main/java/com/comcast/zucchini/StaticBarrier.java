package com.comcast.zucchini;

import java.util.concurrent.Phaser;

/**
 * A class for internal use only that creates a staticly sized barrier sync.
 *
 * This is here because certain versions of java had problems with the BarrierSync and did not perform as expected.
 *
 * @author Andrew Benton
 */
class StaticBarrier {
    private Phaser phase;
    private int parties;
    private long order;

    /**
     * Create the StaticBarrier with a fixed number of parties.
     */
    StaticBarrier(int parties) {
        this.parties = parties;
        this.phase = new Phaser(this.parties);
        this.order = 0;
    }

    /**
     * An internal method to keep track of the order of party completion.
     */
    private synchronized int arrive() {
        return (int)this.order++ % this.parties;
    }

    /**
     * Wait until all parties have reached this point, and then return an integer indicating the order in which they are released.
     */
    int await() {
        Thread.interrupted(); //clear any interrupt.  We don't want it hanging around, as this may not be interrupted.

        this.phase.arriveAndAwaitAdvance();

        return this.arrive();
    }
}
