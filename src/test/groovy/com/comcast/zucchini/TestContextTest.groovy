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
package com.comcast.zucchini

import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertFalse

import org.testng.annotations.Test

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
