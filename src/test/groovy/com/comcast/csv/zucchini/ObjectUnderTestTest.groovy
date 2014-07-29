package com.comcast.csv.zucchini

import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertFalse

import org.testng.annotations.Test

import com.comcast.csv.zucchini.ObjectUnderTest;

class ObjectUnderTestTest {

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
            ObjectUnderTest.set(expected)
            assertEquals(ObjectUnderTest.get(), expected)

            for (int i = 0; i < 10; i++) {
                Thread.sleep((int) Math.random() * 50)
                assertEquals(ObjectUnderTest.get(), expected)
            }

            ObjectUnderTest.remove()
            Thread.sleep((int) Math.random() * 50)
            assertEquals(ObjectUnderTest.get(), null)

            fail = false
        }
    }
}
