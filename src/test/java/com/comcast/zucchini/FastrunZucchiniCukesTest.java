/**
 * Copyright 2014 Comcast Cable Communications Management, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.comcast.zucchini;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import java.util.LinkedList;
import java.util.HashMap;
import java.util.Iterator;

import com.comcast.zucchini.AbstractZucchiniTest;
import com.comcast.zucchini.TestContext;
import com.comcast.zucchini.ZucchiniOutput;
import com.comcast.zucchini.glue.ZucchiniFastrunGlue;

import cucumber.api.CucumberOptions;
import cucumber.runtime.model.CucumberFeature;

@CucumberOptions(
    glue     = {"com.comcast.zucchini.glue"},
    features = {"src/test/resources"},
    tags = {"@FASTRUN-TEST"}
    )
@ZucchiniOutput()
class FastrunZucchiniCukesTest extends AbstractZucchiniTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(FastrunZucchiniCukesTest.class);

    private String[] contextNames = new String[]{"fastrun--asparagus", "fastrun--carrots", "fastrun--potato"};

    @Override
    public List<TestContext> getTestContexts() {
        List<TestContext> contexts = new LinkedList<TestContext>();

        for(String name : contextNames) {
            contexts.add(new TestContext(name));
        }

        return contexts;
    }

    @Override
    public boolean isFastrun() {
        return true;
    }

    private class TestIterator<T> implements Iterator<T> {
        Iterator<T> iter;
        AtomicInteger counter = new AtomicInteger(0);

        public TestIterator(Iterator<T> it) {
            this.iter = it;
        }

        @Override
        public boolean hasNext() {
            return this.iter.hasNext();
        }

        @Override
        public T next() {
            T rv = this.iter.next();
            /* Hardcode this for our feature file
             * Should only be called 5 times (based on feature files)
             */
            int SCENARIOS_RUN_COUNT = 5;
            int calledCount = counter.incrementAndGet();
            Assert.assertTrue(calledCount <= SCENARIOS_RUN_COUNT, "The next() ["+calledCount+"] operator should only return ["+SCENARIOS_RUN_COUNT+"] times");
            return rv;
        }

        @Override
        public void remove() {
            this.iter.remove();
        }

    }

    @Override
    public void cleanup(TestContext out) {
        ZucchiniFastrunGlue.verifyNumberTestContextsRun(contextNames.length);
    }

    private Iterator<CucumberFeatureHolder> singleton;
    private TestIterator<CucumberFeatureHolder> wrapped;

    @Override
    public Iterator<CucumberFeatureHolder> fastrunIteratorFactory(Iterator<CucumberFeature> iterator) {
        Iterator<CucumberFeatureHolder> response = super.fastrunIteratorFactory(iterator);

        if (null == singleton) {
            synchronized (this) {
                if (null == singleton) {
                    singleton = response;
                    wrapped = new TestIterator<CucumberFeatureHolder>(response);
                }
            }
        }
        Assert.assertTrue(singleton == response, "The iterators returned from fastrun should be the same one as it is a singleton");

        return wrapped;
    }



}
