package com.comcast.zucchini;

import java.util.List;
import java.util.LinkedList;
import java.util.HashMap;

import com.comcast.zucchini.AbstractZucchiniTest;
import com.comcast.zucchini.TestContext;
import com.comcast.zucchini.ZucchiniOutput;

import cucumber.api.CucumberOptions;

@CucumberOptions(
    glue = {"com.comcast.zucchini.glue"},
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
