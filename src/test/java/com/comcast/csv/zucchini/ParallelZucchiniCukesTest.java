package com.comcast.csv.zucchini;

import java.util.List;
import java.util.LinkedList;
import java.util.HashMap;
import cucumber.api.CucumberOptions;

@CucumberOptions(
    glue     = {"com.comcast.csv.zucchini.glue"},
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
