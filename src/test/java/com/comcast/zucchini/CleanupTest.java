package com.comcast.zucchini;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by djerus200 on 5/4/16.
 */
public class CleanupTest {

    @Test
    public void testCleanup() throws Throwable {

        final AtomicBoolean cleanupFlag = new AtomicBoolean(false);

        AbstractZucchiniTest azt = new AbstractZucchiniTest() {
            private static final String TCNAME = "testCleanupContext";

            @Override
            public List<TestContext> getTestContexts() {
                return Arrays.asList(new TestContext(TCNAME));
            }

            @Override
            public void setup(TestContext out) {
                throw new RuntimeException("Failing on purpose to test that cleanup will be invoked");
            }

            @Override
            public void cleanup(TestContext out) {
                cleanupFlag.set(true);
            }

            @Override
            public boolean isParallel() {
                return false;

            }

            @Override
            List<String> ignoredTests() {
                return Arrays.asList(TCNAME);
            }
        };

        try {
            azt.run();
            Assert.fail("Should've failed");
        }
        catch (AssertionError e) {
            if ("Should've failed".equals(e.getMessage())) {
                throw e;
            }
            // swallow exception as we expect test to fail due to exception in 'setup'
        }

        Assert.assertTrue(cleanupFlag.get(), "Cleanup was never run");
    }
}
