package com.comcast.zucchini.glue;

import org.testng.Assert;

import com.comcast.zucchini.TestContext;
import com.comcast.zucchini.Veggie;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;

public class ZucchiniTestGlue {

    @Given("The vegetable is (clean|dirty)")
    public void givenCleanState(String clean_state) {
        Veggie veggie = TestContext.getCurrent().get("veggie");
        veggie.clean = (clean_state.equals("clean"));
    }

    @And("The vegetable is (cooked|raw)")
    public void andCookedState(String cooked_state) {
        Veggie veggie = TestContext.getCurrent().get("veggie");
        veggie.cooked = (cooked_state.equals("cooked"));
    }

    @And("The vegetable tastes (.*)")
    public void verifyTaste(String taste) {
        Veggie veggie = TestContext.getCurrent().get("veggie");
        Assert.assertEquals(veggie.getTaste(), taste);
    }
}
