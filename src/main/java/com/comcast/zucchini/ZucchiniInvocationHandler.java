package com.comcast.zucchini;

import cucumber.runtime.Utils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * This class is non-essential for now, however it will be useful for future extension of Zucchini functionality.
 *
 * Currently, the only feature that it adds is a lock around all invocations, which doesn't visibly hurt performance and provides more readable output.
 *
 * @author Andrew Benton
 */
class ZucchiniInvocationHandler implements InvocationHandler {

    private static Object lock = new Object();
    private Object baseProxy;
    private Class type;

    /**
     * Create the ZucchiniInvocationHandler so that it calls against `baseProxy` and has the internal class `type`.
     *
     * @param baseProxy The object that will be called when calls are made to this InvocationHandler
     * @param type The type of class that the `baseProxy` internally represents
     */
    public ZucchiniInvocationHandler(Object baseProxy, Class type) {
        this.baseProxy = baseProxy;
        this.type = type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object invoke(Object target, Method method, Object[] args) throws Throwable {
        synchronized(ZucchiniInvocationHandler.lock) {
            Utils.invoke(this.baseProxy, method, 0, args);
        }

        return null;
    }
}
