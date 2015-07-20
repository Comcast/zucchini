package com.comcast.csv.zucchini.glue

import org.testng.Assert

import com.comcast.csv.zucchini.TestContext
import com.comcast.csv.zucchini.Veggie

import cucumber.api.java.en.And
import cucumber.api.java.en.Given
import cucumber.api.java.en.Then

class ZucchiniTestMultiple {

    @Given('The vegetable exists')
    void 'one'() {
    }

    @And('There is more than one')
    void 'two'() {
    }

    @Then('You have at least two vegetables')
    void 'done'() {
    }
}
