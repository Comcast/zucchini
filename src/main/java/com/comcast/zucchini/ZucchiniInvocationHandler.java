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
     * Create the ZucchiniInvocationHandler so that it calls against <code>baseProxy</code> and has the internal class <code>type</code>.
     *
     * @param baseProxy The object that will be called when calls are made to this InvocationHandler
     * @param type The type of class that the <code>baseProxy</code> internally represents
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
