package com.comcast.csv.zucchini

import cucumber.api.CucumberOptions


@CucumberOptions(
    glue     = ["com.comcast.csv.zucchini.glue"],
    features = ["src/test/resources"]
)
@ZucchiniOutput('target/zucchini.json')
class ParallelZucchiniCukesTest extends AbstractZucchiniTest {

    @Override
    public List<TestContext> getTestContexts() {
        List<TestContext> contexts = []
        
        contexts.push(new TestContext('asparagus', [veggie : new Veggie()]))
        contexts.push(new TestContext('carrots',   [veggie : new Veggie()]))
        contexts.push(new TestContext('cabbages',  [veggie : new Veggie()]))
        contexts.push(new TestContext('onions',    [veggie : new Veggie()]))
        contexts.push(new TestContext('celery',    [veggie : new Veggie()]))
        contexts.push(new TestContext('turnips',   [veggie : new Veggie()]))
        
        return contexts
    }
    
    
}
