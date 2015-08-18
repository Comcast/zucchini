package com.comcast.zucchini;

import java.util.concurrent.Phaser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class StaticBarrier {
    private static Logger logger = LoggerFactory.getLogger(StaticBarrier.class);

    private Phaser phase;
    private int parties;
    private long order;

    StaticBarrier(int parties) {
        this.parties = parties;
        this.phase = new Phaser(this.parties);
        logger.debug("Creating StaticBarrier with {} parties", this.phase.getRegisteredParties());
        this.order = 0;
    }

    private synchronized int arrive() {
        return (int)this.order++ % this.parties;
    }

    int await() {
        Thread.interrupted(); //clear any interrupt.  We don't want it hanging around, as this may not be interrupted.

        this.phase.arriveAndAwaitAdvance();

        return this.arrive();
    }
}
