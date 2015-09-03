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
import java.util.LinkedList;
import java.util.HashMap;

import com.comcast.zucchini.AbstractZucchiniTest;
import com.comcast.zucchini.TestContext;
import com.comcast.zucchini.ZucchiniOutput;

import cucumber.api.CucumberOptions;

@CucumberOptions(
    glue     = {"com.comcast.zucchini.glue"},
    features = {"src/test/resources"},
    tags = {"@STD-TEST"}
    )
@ZucchiniOutput()
class ParallelZucchiniCukesTest extends AbstractZucchiniTest {

    @Override
    public List<TestContext> getTestContexts() {
        List<TestContext> contexts = new LinkedList<TestContext>();

        String[] inputs = new String[]{"asparagus", "carrots", "cabbages", "onions", "celery", "turnips"};

        for(int i = 0; i < inputs.length; i++) {
            contexts.add(new TestContext(inputs[i], new HashMap<String, Object>() {{
                put("veggie", new Veggie());
            }}));
        }

        return contexts;
    }

}
