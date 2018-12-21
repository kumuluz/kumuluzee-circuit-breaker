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
package com.kumuluz.ee.fault.tolerance.interceptors;

import com.kumuluz.ee.fault.tolerance.annotations.FallbackBinding;
import com.kumuluz.ee.fault.tolerance.interfaces.FaultToleranceUtil;
import org.jboss.weld.context.RequestContext;
import org.jboss.weld.context.unbound.Unbound;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

/**
 * Interceptor for {@link FallbackBinding} annotation and consequently for
 * {@link org.eclipse.microprofile.faulttolerance.Fallback} annotation.
 *
 * @author Urban Malc
 * @since 1.1.0
 */
@FallbackBinding
@Interceptor
@Priority(Interceptor.Priority.PLATFORM_AFTER + FaultToleranceInterceptorPriority.FALLBACK)
public class FallbackInterceptor {

    @Inject
    private FaultToleranceUtil faultToleranceUtil;

    @Inject
    @Unbound
    private RequestContext requestContext;

    @AroundInvoke
    public Object executeFaultTolerance(InvocationContext invocationContext) throws Exception {

        if (FaultToleranceInterceptorPriority.shouldExecute(invocationContext))
            return faultToleranceUtil.execute(invocationContext, requestContext);
        else
            return invocationContext.proceed();
    }
}
