package com.comcast.csv.zucchini;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import org.testng.Assert;
import org.testng.annotations.Test;


/**
 * Runs Zucchini tests in serial (one after the other). See {@link AbstractZucchiniTest} for more details.
 *
 * @author Clark Malmgren
 */
public abstract class AbstractSerialZucchiniTest extends AbstractZucchiniTest {

    @Override
    public void verifyProperInheritence() {
        // Just override the method to keep keep the default exception from being thrown
    }

    /**
     * Runs all cucumber features and scenarios for this class against each TestContext
     * in serial (no concurrency).
     */
    @Test
    public void run() {
        boolean failed = false

        contexts.each {
            try {
                run(it)
            } catch (Throwable t) {
                failed = true
                t.printStackTrace()
            }
        }

        Assert.assertFalse(failed, '')
    }
}
