package com.comcast.csv.cucumber.devices

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Simple class for storing and retrieving devices based on the currently running thread. This
 * is useful because the "given", "when" and "then" implementations in cucumber do not share any
 * common DI framework.
 * 
 * @author Clark Malmgren
 */
class Devices {
    
    private static final ThreadLocal cache = new ThreadLocal()
    private static final Logger logger = LoggerFactory.getLogger(Devices.class)
    
    /**
     * Set the device for this thread run. This should be constructor for the jUnit or TestNG java "Test"
     * class. Example:
     * 
     * <pre>
     * @RunWith(Cucumber.class)
     * @CucumberOptions(...)
     * public class MyTest {
     *   public MyTest() {
     *     HawtDevice device = ...;
     *     Boxes.setLocal(device);
     *   }
     * }
     * </pre>
     * 
     * @param device the device for this thread (suite run)
     */
    public static <D> void setLocal(D device) {
        logger.debug("set device for ${Thread.currentThread().getName()}: ${device}")
        cache.set(device)
    }
    
    /**
     * Return the local device for this run of cucumber. This will be the device that was registered
     * using {@link Devices#setLocal(D)}
     * 
     * @return the box under test
     */
    public static <D> D getLocal() {
        D device = cache.get()
        logger.debug("get device for ${Thread.currentThread().getName()} ${device}")
        return device
    }
    
    /**
     * Release the local device for this run of cucumber from thread local storage. This DOES NOT
     * actually release any physical hardware reservations which must be done independently. This
     * simply removes the internal variable so all future calls to {@link Devices#getLocal()} will
     * return <code>null</code>.
     */
    public static void releaseLocal() {
        logger.debug("release device for ${Thread.currentThread().getName()}")
        cache.remove()
    }
}