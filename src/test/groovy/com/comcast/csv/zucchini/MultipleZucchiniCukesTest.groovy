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

        contexts.push(new TestContext('potato'));
        contexts.push(new TestContext('tomato'));

        return contexts;
    }
}
