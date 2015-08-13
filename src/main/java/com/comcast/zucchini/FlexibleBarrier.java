package com.comcast.zucchini;

import java.util.HashMap;
import java.util.HashSet;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class FlexibleBarrier {
    private static Logger logger = LoggerFactory.getLogger(FlexibleBarrier.class);

    AbstractZucchiniTest azt;
    CountDownLatch cdl;
    //HashSet<TestContext> residentThreads;
    HashSet<TestContext> arrivedThreads;

    FlexibleBarrier(AbstractZucchiniTest azt) {
        this(azt, azt.contexts.size());
    }

    FlexibleBarrier(AbstractZucchiniTest azt, int size) {
        this.azt = azt;
        this.cdl = new CountDownLatch(size);
        this.arrivedThreads = new HashSet<TestContext>();
    }

    void unlock() {
        synchronized(this) {
            //force all late tests to fail
            for(TestContext tc : this.azt.contexts) {
                //if the thread has not arrived or already been registered as failed, register it as failed, and stop it
                if(!(this.arrivedThreads.contains(tc) || this.azt.failedContexts.contains(tc))) {
                    azt.failedContexts.add(tc);
                    tc.getThread().stop();
                }
            }

            //release all of the threads that are currently waiting
            long missingCount = this.cdl.getCount();
            for(long i = 0; i < missingCount; i++) {
                this.cdl.countDown();
            }

            //reset the count down latch
            this.reset();
        }
    }

    int await() {
        return this.await(0);
    }

    int await(int milliseconds) {
        int ret = -1;

        synchronized(this) {
            ret = this.arrivedThreads.size();

            this.arrivedThreads.add(TestContext.getCurrent());

            this.cdl.countDown();
        }

        try {
            if(milliseconds < 1)
                this.cdl.await();
            else
                this.cdl.await(milliseconds, TimeUnit.MILLISECONDS);
        }
        catch(InterruptedException ex) {
            //ignore for now
        }

        if(ret == 0)
            synchronized(this) {
                this.unlock();
            }

        return ret;
    }

    void dec() {
        this.cdl.countDown();
    }

    void reset() {
        synchronized(this) {
            if(this.azt.isParallel())
                this.cdl = new CountDownLatch(this.azt.contexts.size() - this.azt.failedContexts.size());
            else
                this.cdl = new CountDownLatch(1);

            this.arrivedThreads.clear();
        }
    }
}
