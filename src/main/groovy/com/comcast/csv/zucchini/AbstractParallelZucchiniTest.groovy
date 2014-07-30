package com.comcast.csv.zucchini;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import org.testng.annotations.Test;


/**
 * Runs Zucchini tests in parallel. See {@link AbstractZucchiniTest} for more details.
 * 
 * Note: This can only handle up to 64 max targets to test.
 * 
 * @author Clark Malmgren
 */
public abstract class AbstractParallelZucchiniTest extends AbstractZucchiniTest {

    private AtomicInteger count = new AtomicInteger(0)

    @Override
    public void verifyProperInheritence() {
        // Just override the method to keep keep the default exception from being thrown
    }

    /**
     * Runs the test in parallel.
     */
    @Test(threadPoolSize = 64, invocationCount = 64)
    public void run() {
        int index = count.getAndIncrement();
        if (index < contexts.size()) {
            run(contexts[index])
        }
    }
}
