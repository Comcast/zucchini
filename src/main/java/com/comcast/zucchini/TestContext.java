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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Simple class for storing and retrieving objects that are being test based on the currently running
 * thread. This is useful because the "given", "when" and "then" implementations in cucumber do not
 * share any common DI framework.
 *
 * @author Clark Malmgren
 */
public class TestContext {

    private static final ThreadLocal<TestContext> LOCAL = new ThreadLocal<TestContext>();

    String name;
    private Map<String, Object> beans;
    private Thread owningThread;
    volatile boolean canKill = false;
    AbstractZucchiniTest parentTest;

    /**
     * Set the test context for this thread run. This should be only be called from a
     * {@link AbstractZucchiniTest} methods unless you really know what you are doing.
     *
     * @param context the object under test for this thread (suite run)
     */
    public static void setCurrent(TestContext context) {
        LOCAL.set(context);
        context.owningThread = Thread.currentThread();
    }

    /**
     * Return the current test context for this run of cucumber. This will be the object that
     * was registered using {@link TestContext#set(String, Object)}
     *
     * @return the test context
     */
    public static TestContext getCurrent() {
        return LOCAL.get();
    }

    /**
     * Release the test context for this run of cucumber from local storage. This DOES NOT
     * actually release any physical hardware reservations which must be done independently. This
     * simply removes the internal variable so all future calls to {@link TestContext#get(String)} will
     * return <code>null</code>.
     *
     * This should be only be called from the {@link AbstractZucchiniTest} methods unless you
     * really know what you are doing.
     */
    public static void removeCurrent() {
        LOCAL.remove();
    }

    /**
     * Returns set of bean names
     *
     * @return Set of bean names
     */
    public Set<String> getAllKeys() {
        return this.beans.keySet();
    }

    /**
     * Constructs a new empty TestContext
     *
     * @param name Name to be assigned to the context
     */
    public TestContext(String name) {
        this(name, new HashMap<String, Object>());
    }

    /**
     * Create a new TestContext pre-populated with the given beans.
     *
     * @param name Name to be assigned to the context
     * @param beans the named objects in this test context
     */
    public TestContext(String name, Map<String, Object> beans) {
        this.name = name;
        this.beans = beans;
        this.canKill = false;
    }

    Thread getThread() {
        return this.owningThread;
    }

    AbstractZucchiniTest getParentTest() {
        return this.parentTest;
    }

    /**
     * Returns the name of this TestContext. Names are required to make Zucchini tests
     * easier to debug and understand.
     *
     * @return the name of this test context
     */
    public String name() {
        return name;
    }

    /**
     * Get an object in this test context by name
     *
     * @param <T> Type of the object
     * @param key the name of the object in this test context
     * @return the test object
     */
    public <T> T get(String key) {
        return (T)beans.get(key);
    }

    /**
     * Put a named object into this test context
     *
     * @param <T> Type of the object
     * @param key the name of the object
     * @param val the test object
     */
    public <T> void set(String key, T val) {
        beans.put(key, val);
    }
}
