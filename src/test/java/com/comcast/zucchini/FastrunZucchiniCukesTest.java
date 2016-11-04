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

    @Override
    public List<TestContext> getTestContexts() {
        List<TestContext> contexts = new LinkedList<TestContext>();

        String[] inputs = new String[]{"asparagus", "carrots"};

        for(int i = 0; i < inputs.length; i++) {
            contexts.add(new TestContext("Runfast--"+inputs[i], new HashMap<String, Object>() {{
                put("veggie", new Veggie());
            }}));
        }

        return contexts;
    }

    @Override
    public boolean isRunfast() {
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
             * Should only be called twice (based on feature files)
             */
            int calledCount = counter.incrementAndGet();
            Assert.assertTrue(calledCount <= 2, "The next() ["+calledCount+"] operator should only return twice");
            return rv;
        }

        @Override
        public void remove() {
            this.iter.remove();
        }

    }

    private Iterator<CucumberFeature> singleton;
    private TestIterator<CucumberFeature> wrapped;

    @Override
    public Iterator<CucumberFeature> fastrunIteratorFactory(Iterator<CucumberFeature> iterator) {
        Iterator<CucumberFeature> response = super.fastrunIteratorFactory(iterator);

        if (null == singleton) {
            synchronized (this) {
                if (null == singleton) {
                    singleton = response;
                    wrapped = new TestIterator<>(response);
                }
            }
        }
        Assert.assertTrue(singleton == response, "The iterators returned from fastrun should be the same one as it is a singleton");

        return wrapped;
    }



}
