package com.comcast.zucchini;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Barrier {
    private static Logger logger = LoggerFactory.getLogger(Barrier.class);

    public static int sync() {
        return Barrier.sync(-1);
    }

    public static int sync(int milliseconds) {
        TestContext tc = TestContext.getCurrent();

        if(tc == null) return -1;

        AbstractZucchiniTest azt = tc.getParentTest();

        return azt.flexBarrier.await(milliseconds);
    }
}
