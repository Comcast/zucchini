package com.comcast.csv.zucchini;

import java.util.List;
import java.util.LinkedList;
import java.util.HashMap;

import cucumber.api.CucumberOptions;

@CucumberOptions(
    glue = {"com.comcast.csv.zucchini.glue"},
    features = {"src/test/resources"},
    tags = {"@MULTI-TEST"}
    )
@ZucchiniOutput()
public class MultipleZucchiniCukesTest extends AbstractZucchiniTest {

    @Override
    public List<TestContext> getTestContexts() {
        List<TestContext> contexts = new LinkedList<TestContext>();

        String[] input = new String[] { "potato", "tomato", "eggplant" };

        for(int i = 0; i < input.length; i++) {
            contexts.add(new TestContext(input[i], new HashMap<String, Object>() {{
                put("veggie", new Veggie());
            }}));
        }

        return contexts;
    }
}
