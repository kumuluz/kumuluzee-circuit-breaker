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
package com.kumuluz.ee.circuit.breaker.interceptors;

import com.kumuluz.ee.circuit.breaker.annotations.CircuitBreaker;
import com.kumuluz.ee.circuit.breaker.annotations.EnableCircuitBreaker;
import com.kumuluz.ee.circuit.breaker.utils.CircuitBreakerUtil;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.lang.reflect.Method;

/**
 * Interceptor for handling circuit breaker execution.
 *
 * @author Luka Šarc
 */
@Interceptor
@EnableCircuitBreaker
@Priority(Interceptor.Priority.PLATFORM_AFTER)
public class EnableCircuitBreakerInterceptor {

    @Inject
    private CircuitBreakerUtil circuitBreakerUtil;

    @AroundInvoke
    public Object executeWithCircuitBreaker(InvocationContext ic) throws Exception {

        Method targetMethod = ic.getMethod();

        if (targetMethod.isAnnotationPresent(CircuitBreaker.class)) {
            ic.getContextData();

            return circuitBreakerUtil.execute(ic);
        } else {
            return ic.proceed();
        }
    }

}
