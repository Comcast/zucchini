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
package com.comcast.zucchini.glue;

import org.testng.Assert;
import static org.testng.Assert.assertEquals;

import com.comcast.zucchini.BarrierTest;
import com.comcast.zucchini.Barrier;
import com.comcast.zucchini.TestContext;
import com.comcast.zucchini.Veggie;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZucchiniBarrierGlue {

    private static int barrierCount = 0;

    private static final Logger LOGGER = LoggerFactory.getLogger(ZucchiniBarrierGlue.class);

    public static String name() {
        return TestContext.getCurrent().name();
    }

    @Given("We have a blank method here")
    public void blank() {}

    @And("We sync fast here")
    public void fast() throws Throwable {
        LOGGER.debug("Entering barrier on " + name());
        Barrier.sync();
        LOGGER.debug("Leaving barrier on "  + name());
        Barrier.sync();
    }

    @Then("We should be just fine")
    public void fine() {}

    @And("We sync slow here")
    public void slow() throws Throwable {
        LOGGER.debug("Entering barrier on " + name());
        if(name().equals("ThreadIdx[0]")) {
            try {
                Thread.sleep(50);
            }
            catch(Throwable t) {
                LOGGER.debug("The sleep was broken.");
            }
        }

        Barrier.sync(2150);
    }

    @Then("Our sync times out and proceeds")
    public void timeout() {}

    @And("We timeout and catch the timeout here")
    public void fail_and_catch_here() throws Throwable {
        if(name().equals("ThreadIdx[0]")) {
            try {
                Barrier.sync(50); //wait 50ms
            }
            catch(Throwable t) {
                LOGGER.error("Sync still passed.", t);
                throw t;
            }
        }
        else {
            try {
                Thread.sleep(1000);
            }
            catch(ThreadDeath ex) {
                //ignore, this is proper behavior
                return;
            }
            catch(Throwable t) {
                LOGGER.error("Thread failed to stop.", t);
                throw t;
            }
        }
    }

    @Then("The last step properly failed")
    public void empty_step() throws Throwable {
    }

    @Given("We are testing thread abort")
    public void abort_test_start() throws Throwable {
        Barrier.sync(); //sync on startup to ensure that previous time-sensitive syncs don't false fail
    }

    @And("We have one thread fail")
    public void one_thread_fail() throws Throwable {
        String ztffp = System.getenv("ZUCCHINI_TEST_FF_PATTERNS");
        if(ztffp == null)
            ztffp = "0";

        if(
                name().equals("ThreadIdx[0]") &&
                ("1").equals(ztffp)
            ) {
            Assert.fail("THIS IS SUPPOSED TO FAIL.");
        }
        else {
            Thread.sleep(100);
        }

        Barrier.sync(2000);
    }

    @Then("Our barrier still runs")
    public void barrier_still_works() throws Throwable {
        Barrier.sync(1000);
    }

    @Given("We have one barrier")
    public void first_barrier() throws Throwable {
        synchronized(this) {
            barrierCount++;
        }
        Barrier.sync();
    }

    @And("We have another barrier")
    public void second_barrier() throws Throwable {
        LOGGER.debug(String.format("BarrierCount for [%s] is %d, and numContexts is %d after sync", name(),  barrierCount, BarrierTest.numContexts));
        if(barrierCount != BarrierTest.numContexts) {
            Assert.fail(String.format("The internal barrierCount[%d] does not match the number of contexts[%d]", barrierCount, BarrierTest.numContexts));
        }
        Barrier.sync();
        synchronized(this) {
            barrierCount--;
        }
    }

    @Then("Our barriers executed in order")
    public void barrier_serial_end() {
        Barrier.sync();
        if(barrierCount != 0) {
            Assert.fail(String.format("The internal barrierCount[%d] was not set to zero.", barrierCount));
        }
    }
}
