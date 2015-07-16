package com.comcast.csv.zucchini;

import cucumber.api.CucumberOptions;

@CucumberOptions(
    glue = ["com.comcast.csv.zucchini.glue"],
    features = ["src/test/resources"],
    tags = ["@MULTI-TEST"]
    )
@ZucchiniOutput()
public class MultipleZucchiniCukesTest extends AbstractZucchiniTest {

    @Override
    public List<TestContext> getTestContexts() {
        List<TestContext> contexts = [];

        contexts.push(new TestContext('potato'  , [veggie: new Veggie()] ));
        contexts.push(new TestContext('tomato'  , [veggie: new Veggie()] ));
        contexts.push(new TestContext('eggplant', [veggie: new Veggie()] ));

        return contexts;
    }
}
