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

import com.comcast.zucchini.Barrier;
import com.comcast.zucchini.TestContext;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZucchiniBarrierFailureGlue {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZucchiniBarrierFailureGlue.class);

    private static final String FF_ENV_NAME = "ZUCCHINI_TEST_FF_PATTERNS";

    @Given("we barrier in the init")
    public void background_init() {
        LOGGER.debug("Background init");

        Integer i = (Integer)TestContext.getCurrent().get("idx");

        try {
            Thread.sleep(i);
        }
        catch(InterruptedException iex) {
            LOGGER.error("Failed to sleep: ", iex);
        }
    }

    @Given("we run this step")
    public void given_step() {
        LOGGER.debug("Scenario setup");
        Barrier.sync();
    }

    @Then("we fail here")
    public void fail_here() throws Throwable {
        String ztffp = System.getenv(ZucchiniBarrierFailureGlue.FF_ENV_NAME);
        if(ztffp == null)
            ztffp = "0";

        if(("1").equals(ztffp)) {
            Barrier.sync();
            Assert.fail("FORCE ALL CONTEXT TEST FAILURE");
        }
    }

    @Then("we don\'t fail")
    public void pass_here() {
        LOGGER.debug("No failure for this scenario.");
    }
}
