package com.comcast.csv.zucchini.glue

import org.testng.Assert

import com.comcast.csv.zucchini.TestContext
import com.comcast.csv.zucchini.Veggie

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
