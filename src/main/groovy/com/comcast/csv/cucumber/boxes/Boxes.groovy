package com.comcast.csv.cucumber.boxes

import com.comcast.video.dawg.common.DawgDevice;

/**
 * Simple class for storing and retrieving devices based on the currently running thread. This
 * is useful because the "given", "when" and "then" implementations in cucumber do not share any
 * common DI framework.
 * 
 * @author Clark Malmgren
 */
class Boxes {
    
    private static final ThreadLocal<DawgDevice> cache = new ThreadLocal<>()
    
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
    public static void setLocal(DawgDevice device) {
        cache.set(device)
    }
    
    /**
     * Return the local device for this run of cucumber.
     * 
     * @return the box under test
     */
    public static DawgDevice getLocal() {
        return cache.get()
    }
    
    public static void releaseLocal() {
        cache.remove()
    }
}