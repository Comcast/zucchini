package com.comcast.csv.zucchini;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple class for storing and retrieving objects that are being test based on the currently running
 * thread. This is useful because the "given", "when" and "then" implementations in cucumber do not
 * share any common DI framework.
 *
 * @author Clark Malmgren
 */
public class TestContext {

    private static final ThreadLocal local = new ThreadLocal();
    private static final Logger logger = LoggerFactory.getLogger(TestContext.class);

    /**
     * Set the test context for this thread run. This should be only be called from a
     * {@link AbstractZucchiniTest} methods unless you really know what you are doing.
     *
     * @param out the object under test for this thread (suite run)
     */
    public static void setCurrent(TestContext context) {
        local.set(context);
    }

    /**
     * Return the current test context for this run of cucumber. This will be the object that
     * was registered using {@link TestContext#set(D)}
     *
     * @return the test context
     */
    public static TestContext getCurrent() {
        return (TestContext)local.get();
    }

    /**
     * Release the test context for this run of cucumber from local storage. This DOES NOT
     * actually release any physical hardware reservations which must be done independently. This
     * simply removes the internal variable so all future calls to {@link TestContext#get()} will
     * return <code>null</code>.
     *
     * This should be only be called from the {@link AbstractZucchiniTest} methods unless you
     * really know what you are doing.
     */
    public static void removeCurrent() {
        local.remove();
    }

    String name;
    private Map<String, Object> beans;

    /**
     * Constructs a new empty TestContext
     */
    public TestContext(String name) {
        this(name, new HashMap<String, Object>());
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
     * Create a new TestContext pre-populated with the given beans.
     *
     * @param beans the named objects in this test context
     */
    public TestContext(String name, Map<String, Object> beans) {
        this.name = name;
        this.beans = beans;
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
     * @param key the name of the object in this test context
     * @return the test object
     */
    public <T> T get(String key) {
        return (T)beans.get(key);
    }

    /**
     * Put a named object into this test context
     *
     * @param key the name of the object
     * @param val the test object
     */
    public <T> void set(String key, T val) {
        beans.put(key, val);
    }
}
