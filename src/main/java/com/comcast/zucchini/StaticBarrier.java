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

import java.util.concurrent.Phaser;

/**
 * A class for internal use only that creates a staticly sized barrier sync.
 *
 * This is here because certain versions of java had problems with the BarrierSync and did not perform as expected.
 *
 * @author Andrew Benton
 */
class StaticBarrier {
    private Phaser phase;
    private int parties;
    private long order;

    /**
     * Create the StaticBarrier with a fixed number of parties.
     */
    StaticBarrier(int parties) {
        this.parties = parties;
        this.phase = new Phaser(this.parties);
        this.order = 0;
    }

    /**
     * An internal method to keep track of the order of party completion.
     */
    private synchronized int arrive() {
        return (int)this.order++ % this.parties;
    }

    /**
     * Wait until all parties have reached this point, and then return an integer indicating the order in which they are released.
     */
    int await() {
        Thread.interrupted(); //clear any interrupt.  We don't want it hanging around, as this may not be interrupted.

        this.phase.arriveAndAwaitAdvance();

        return this.arrive();
    }
}
