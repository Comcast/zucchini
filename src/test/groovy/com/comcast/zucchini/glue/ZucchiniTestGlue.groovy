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
package com.comcast.zucchini.glue

import org.testng.Assert

import com.comcast.zucchini.TestContext
import com.comcast.zucchini.Veggie

import cucumber.api.java.en.And
import cucumber.api.java.en.Given

class ZucchiniTestGlue {

    @Given('The vegetable is (clean|dirty)')
    void 'given clean state'(String clean_state) {
        Veggie veggie = TestContext.current['veggie']
        veggie.clean = (clean_state == 'clean')
    }
    
    @And('The vegetable is (cooked|raw)')
    void 'and cooked state'(String cooked_state) {
        Veggie veggie = TestContext.current['veggie']
        veggie.cooked = (cooked_state == 'cooked')
    }
    
    @And('The vegetable tastes (.*)')
    void 'verify taste'(String taste) {
        Veggie veggie = TestContext.current['veggie']
        Assert.assertEquals(veggie.taste, taste)
    }
}
