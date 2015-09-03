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

/**
 * This creates a barrier sync when using the Zucchini framework.
 *
 * This must be active under an {@link AbstractZucchiniTest}.
 *
 * @author Andrew Benton
 */
public class Barrier {

    protected Barrier() {
    }

    /**
     * Creates a barrier sync that will wait until all {@link TestContext}'s join or fail.
     *
     * @return The order in which the runners are released from the barrier.
     */
    public static int sync() {
        return Barrier.sync(-1);
    }

    /**
     * Creates a barrier sync that will wait until <code>milliseconds</code> after the first TestContext hits the barrier or all {@link TestContext}'s join or fail.
     *
     * @param milliseconds The amount of time allocated to wait until the barrier times out and halts non-waiting threads.
     * @return The order in which the runners are released from the barrier.
     */
    public static int sync(int milliseconds) {
        TestContext tc = TestContext.getCurrent();

        if(tc == null)
            return -1;

        AbstractZucchiniTest azt = tc.getParentTest();

        if(!azt.canBarrier())
            throw new ZucchiniRuntimeException("This test is not configured to allow barriers.");

        if(azt.isParallel())
            return azt.flexBarrier.await(milliseconds);
        else
            return 0;
    }
}
