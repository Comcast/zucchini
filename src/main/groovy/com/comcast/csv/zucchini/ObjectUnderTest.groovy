package com.comcast.csv.zucchini

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Simple class for storing and retrieving object that are being test based on the currently running
 * thread. This is useful because the "given", "when" and "then" implementations in cucumber do not
 * share any common DI framework.
 * 
 * @author Clark Malmgren
 */
class ObjectUnderTest {

    private static final ThreadLocal cache = new ThreadLocal()
    private static final Logger logger = LoggerFactory.getLogger(ObjectUnderTest.class)

    /**
     * Set the object under test for this thread run. This should be only be called from the
     * {@link AbstractZucchiniTest} methods unless you really know what you are doing. 
     * 
     * @param out the object under test for this thread (suite run)
     */
    public static <D> void set(D out) {
        logger.debug("set OUT for ${Thread.currentThread().getName()}: ${out}")
        cache.set(out)
    }

    /**
     * Return the current object under test for this run of cucumber. This will be the object that
     * was registered using {@link ObjectUnderTest#set(D)}
     * 
     * @return the object under test
     */
    public static <D> D get() {
        D out = cache.get()
        logger.debug("get device for ${Thread.currentThread().getName()} ${out}")
        return out
    }

    /**
     * Release the object under testfor this run of cucumber from local storage. This DOES NOT
     * actually release any physical hardware reservations which must be done independently. This
     * simply removes the internal variable so all future calls to {@link ObjectUnderTest#get()} will
     * return <code>null</code>. 
     * 
     * This should be only be called from the {@link AbstractZucchiniTest} methods unless you
     * really know what you are doing.
     */
    public static void remove() {
        logger.debug("release device for ${Thread.currentThread().getName()}")
        cache.remove()
    }
}