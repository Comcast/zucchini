package com.comcast.zucchini;

import java.util.HashMap;

import cucumber.api.SummaryPrinter;
import cucumber.api.StepDefinitionReporter;

import cucumber.runtime.Utils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import gherkin.formatter.Formatter;
import gherkin.formatter.Reporter;

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

    public ZucchiniInvocationHandler(Object baseProxy, Class type) {
        this.baseProxy = baseProxy;
        this.type = type;
    }

    @Override
    public Object invoke(Object target, Method method, Object[] args) throws Throwable {
        synchronized(ZucchiniInvocationHandler.lock) {
            Utils.invoke(this.baseProxy, method, 0, args);
        }

        return null;
    }
}
