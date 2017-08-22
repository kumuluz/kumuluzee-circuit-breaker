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
package com.kumuluz.ee.fault.tolerance.interfaces;

import com.kumuluz.ee.fault.tolerance.models.ConfigurationProperty;
import com.kumuluz.ee.fault.tolerance.models.ExecutionMetadata;
import org.jboss.weld.context.RequestContext;

import javax.interceptor.InvocationContext;

/**
 * Interface for implementing circuit breaker executor for specific
 * circuit breaker library.
 *
 * @author Luka Šarc
 */
public interface FaultToleranceExecutor {

    String getName();

    Object execute(InvocationContext invocationContext, RequestContext requestContext, ExecutionMetadata coreConfig) throws Exception;

    void setPropertyValue(ConfigurationProperty property);

    ConfigurationProperty getPropertyValue(ConfigurationProperty property);

}