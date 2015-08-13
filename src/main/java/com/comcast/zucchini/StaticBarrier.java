package com.comcast.zucchini;

import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class StaticBarrier {
    private static Logger logger = LoggerFactory.getLogger(StaticBarrier.class);

    CountDownLatch cdl;
    int parties;

    StaticBarrier(int parties) {
        this.parties = parties;
        this.cdl = new CountDownLatch(this.parties);
    }

    int await() {
        int ret = -1;

        Thread.currentThread().interrupted(); //clear any interrupt.  We don't want it hanging around, as this may not be interrupted.

        synchronized(this) {
            this.cdl.countDown();
            ret = (int)this.cdl.getCount();
        }

        try {
            this.cdl.await();
        }
        catch(InterruptedException ex) {
            logger.error("ERROR: {}", ex);
        }

        return ret;
    }

    void reset() {
        synchronized(this) {
            this.cdl = new CountDownLatch(this.parties);
        }
    }
}
