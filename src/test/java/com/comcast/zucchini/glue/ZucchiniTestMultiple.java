package com.comcast.zucchini.glue;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

public class ZucchiniTestMultiple {

    @Given("The vegetable exists")
    public void one() {
    }

    @And("There is more than one")
    public void two() {
    }

    @Then("You have at least two vegetables")
    public void done() {
    }
}
