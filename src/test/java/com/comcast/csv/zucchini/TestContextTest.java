package com.comcast.csv.zucchini;

import java.util.List;
import java.util.LinkedList;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

import org.testng.annotations.Test;

import com.comcast.csv.zucchini.TestContext;

class TestContextTest {

    @Test
    void verifyThreadUniqueValues() {
        LinkedList<TestThread> threads = new LinkedList<TestThread>();

        TestThread tmp;

        String[] expected = new String[]{"apples", "bananas", "carrots", "diapers", "elephants"};

        for(int i = 0; i < expected.length; i++) {
            tmp = new TestThread();
            tmp.expected = expected[i];
            threads.add(tmp);
        }

        for(TestThread t : threads) {
            t.start();
        }

        for(TestThread t : threads) {
            try {
                t.join();
            }
            catch(Throwable e) {
                System.out.println("FATAL ERROR: " + e.toString());
            }
        }

        for(TestThread t : threads) {
            assertFalse(t.fail);
        }
    }

    class TestThread extends Thread {
        String expected;
        boolean fail = true;

        @Override
        public void run() {
            TestContext.setCurrent(new TestContext(expected));
            assertEquals(TestContext.getCurrent().name, expected);

            for (int i = 0; i < 10; i++) {
                try {
                    Thread.sleep((int) Math.random() * 50);
                }
                catch(Throwable e) {
                    System.out.println("Thread failed to sleep.");
                }
                assertEquals(TestContext.getCurrent().name, expected);
            }

            TestContext.removeCurrent();
            try {
                Thread.sleep((int) Math.random() * 50);
            }
            catch(Throwable e) {
                System.out.println("Thread failed to sleep.");
            }
            assertEquals(TestContext.getCurrent(), null);

            fail = false;
        }
    }
}
