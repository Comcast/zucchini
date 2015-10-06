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

import com.comcast.zucchini.BarrierTest;
import com.comcast.zucchini.Barrier;
import com.comcast.zucchini.TestContext;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZucchiniScenarioOutlineBarrierGlue {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZucchiniScenarioOutlineBarrierGlue.class);

    public static String name() {
        return TestContext.getCurrent().name();
    }

    @Given("^\\s*we set the name to ([A-Za-z]+)\\s*$")
    public void givenSetName(String name) {
        TestContext ctx = TestContext.getCurrent();
        ctx.set("name", name);
        LOGGER.debug("Beginning step one with name: {}", name);
    }

    @And("^\\s*the first context to finish fails\\s*$")
    public void andContextFails() {
        int idx = Barrier.sync();

        LOGGER.debug("Step two started");

        String ztffp = System.getenv("ZUCCHINI_TEST_FF_PATTERNS");
        if(ztffp == null)
            ztffp = "0";

        if(name().equals("ThreadIdx[0]") && ("1").equals(ztffp)) {
            if(idx == 0)
                throw new RuntimeException(String.format("Purposeful failure on ctx[%s]", (String)TestContext.getCurrent().get("name")));
        }

        LOGGER.debug("Step two finished");
    }

    @Then("^\\s*the test terminates\\s*$")
    public void thenTestTerminates() {
        Barrier.sync();
        LOGGER.debug("Test finished");
    }
}
