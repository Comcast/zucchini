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

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import org.testng.Assert;

import java.util.HashMap;

import cucumber.api.CucumberOptions;

@CucumberOptions(
    glue = {"com.comcast.zucchini.glue"},
    features = {"src/test/resources"},
    tags = {"@EMPTY-FEATURE"}
    )
@ZucchiniOutput()
public class IllegalParametersTest extends AbstractZucchiniTest {

    public static int numContexts = 3;

    @Override
    public List<TestContext> getTestContexts() {
        List<TestContext> contexts = new ArrayList<TestContext>();

        for(int i = 0; i < numContexts; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("idx", new Integer(i));
            contexts.add(new TestContext(String.format("ThreadIdx[%d]", i), map));
        }

        return contexts;
    }

    @Override
    public void run() {
        boolean hitException = false;
        try {
            super.run();
        } catch (Exception e) {
            hitException = true;
        }
        Assert.assertTrue(hitException, "The test MUST error out because we are using illegal run configuratino");
    }

    @Override
    public boolean canBarrier() {
        return true;
    }

    @Override
    public boolean isFastrun() {
        return true;
    }
}
