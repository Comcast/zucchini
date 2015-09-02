package com.comcast.zucchini;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

import cucumber.api.CucumberOptions;

@CucumberOptions(
    glue = {"com.comcast.zucchini.glue"},
    features = {"src/test/resources"},
    tags = {"@BARRIER"}
    )
@ZucchiniOutput()
public class BarrierTest extends AbstractZucchiniTest {

    public static int numContexts = 5;

    @Override
    public List<TestContext> getTestContexts() {
        List<TestContext> contexts = new ArrayList<TestContext>();

        for(int i = 0; i < numContexts; i++) {
            contexts.add(new TestContext(String.format("ThreadIdx[%d]", i), new HashMap<String, Object>()));
        }

        return contexts;
    }

    @Override
    public boolean canBarrier() {
        return true;
    }
}
