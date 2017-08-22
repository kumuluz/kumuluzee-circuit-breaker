/*
 *  Copyright (c) 2014-2017 Kumuluz and/or its affiliates
 *  and other contributors as indicated by the @author tags and
 *  the contributor list.
 *
 *  Licensed under the MIT License (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  https://opensource.org/licenses/MIT
 *
 *  The software is provided "AS IS", WITHOUT WARRANTY OF ANY KIND, express or
 *  implied, including but not limited to the warranties of merchantability,
 *  fitness for a particular purpose and noninfringement. in no event shall the
 *  authors or copyright holders be liable for any claim, damages or other
 *  liability, whether in an action of contract, tort or otherwise, arising from,
 *  out of or in connection with the software or the use or other dealings in the
 *  software. See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.kumuluz.ee.fault.tolerance.models;

import com.kumuluz.ee.fault.tolerance.interfaces.FallbackHandler;

import java.lang.reflect.Method;

/**
 * Model for holding information circuit breaker needs to execute method.
 *
 * @author Luka Šarc
 */
public class ExecutionMetadata {

    protected final Class targetClass;
    protected final Method method;
    protected final Class<? extends FallbackHandler> fallbackHandlerClass;
    protected final Method fallbackMethod;

    protected final String commandKey;
    protected final String groupKey;
    protected final Class<? extends Throwable>[] failOn;

    public ExecutionMetadata(Class targetClass, Method method, Class<? extends FallbackHandler> fallbackHandlerClass, Method fallbackMethod, String commandKey, String groupKey, Class<? extends Throwable>[] failOn) {
        this.targetClass = targetClass;
        this.method = method;
        this.fallbackHandlerClass = fallbackHandlerClass;
        this.fallbackMethod = fallbackMethod;
        this.commandKey = commandKey;
        this.groupKey = groupKey;
        this.failOn = failOn;
    }

    public Method getMethod() {
        return method;
    }

    public Class<? extends FallbackHandler> getFallbackHandlerClass() {
        return fallbackHandlerClass;
    }

    public Method getFallbackMethod() {
        return fallbackMethod;
    }

    public String getCommandKey() {
        return commandKey;
    }

    public String getGroupKey() {
        return groupKey;
    }

    public Class<? extends Throwable>[] getFailOn() {
        return failOn;
    }

    public Class getTargetClass() {
        return targetClass;
    }

}