/**
 * Copyright 2014 Comcast Cable Communications Management, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.comcast.zucchini;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

class TestContextTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestContextTest.class);

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
                LOGGER.error("FATAL ERROR: " + e.getMessage());
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
                    LOGGER.error("Thread failed to sleep.");
                }
                assertEquals(TestContext.getCurrent().name, expected);
            }

            TestContext.removeCurrent();
            try {
                Thread.sleep((int) Math.random() * 50);
            }
            catch(Throwable e) {
                LOGGER.error("Thread failed to sleep.");
            }
            assertEquals(TestContext.getCurrent(), null);

            fail = false;
        }
    }
}
