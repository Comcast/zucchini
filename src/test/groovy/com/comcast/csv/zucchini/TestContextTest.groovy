package com.comcast.csv.zucchini

import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertFalse

import org.testng.annotations.Test

import com.comcast.csv.zucchini.TestContext;

class TestContextTest {

    @Test
    void 'Verify Thread Unique Values'() {
        List threads = new ArrayList()

        threads.push(new TestThread(expected: "apples"));
        threads.push(new TestThread(expected: "bananas"));
        threads.push(new TestThread(expected: "carrots"));
        threads.push(new TestThread(expected: "diapers"));
        threads.push(new TestThread(expected: "elephants"));

        threads.each { it.start() }
        threads.each { it.join() }

        threads.each { assertFalse(it.fail) }
    }

    class TestThread extends Thread {
        String expected;
        boolean fail = true

        @Override
        public void run() {
            TestContext.setCurrent(new TestContext(expected))
            assertEquals(TestContext.getCurrent().name, expected)

            for (int i = 0; i < 10; i++) {
                Thread.sleep((int) Math.random() * 50)
                assertEquals(TestContext.getCurrent().name, expected)
            }

            TestContext.removeCurrent()
            Thread.sleep((int) Math.random() * 50)
            assertEquals(TestContext.getCurrent(), null)

            fail = false
        }
    }
}
