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

import org.apache.commons.lang.mutable.MutableInt;

/**
 * Provides the thread-wise context in order to run the parallel Zucchini tests.
 *
 * @author Andrew Benton
 */
public class TestRunner implements Runnable {

    private AbstractZucchiniTest test;
    private TestContext tc;
    private MutableInt mi;

    /**
     * Create a test runner linked to the test.
     *
     * This should only be used for parallel runs
     *
     * @param test The AbstractZucchiniTest that this will run on top of
     * @param tc The test context that this will be linked to
     * @param mi The running count of the number of failures
     */
    public TestRunner(AbstractZucchiniTest test, TestContext tc, MutableInt mi) {
        this.test = test;
        this.tc = tc;
        this.mi = mi;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        if(!this.test.runWith(this.tc)) {
            synchronized(this.mi) {
                this.mi.increment();
            }
        }
    }

    @Override
    public String toString() {
        return super.toString() + String.format("[%s, %s]", this.tc.toString(), this.mi.toInteger());
    }
}
