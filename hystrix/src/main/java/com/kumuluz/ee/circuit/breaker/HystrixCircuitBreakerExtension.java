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
package com.kumuluz.ee.circuit.breaker;

import com.kumuluz.ee.common.Extension;
import com.kumuluz.ee.common.config.EeConfig;
import com.kumuluz.ee.common.dependencies.EeComponentDependency;
import com.kumuluz.ee.common.dependencies.EeComponentType;
import com.kumuluz.ee.common.dependencies.EeExtensionDef;
import com.kumuluz.ee.common.dependencies.EeExtensionType;
import com.kumuluz.ee.common.wrapper.KumuluzServerWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * KumuluzEE framework extension for Hystrix based circuit breaker
 *
 * @author Luka Šarc
 */
@EeExtensionDef(name = "hystrix", type = EeExtensionType.CIRCUIT_BREAKER)
@EeComponentDependency(EeComponentType.CDI)
public class HystrixCircuitBreakerExtension implements Extension {

    private static final Logger log = LoggerFactory.getLogger(HystrixCircuitBreakerExtension.class);

    @Override
    public void init(KumuluzServerWrapper kumuluzServerWrapper, EeConfig eeConfig) {
        log.info("Initialising circuit breaker implemented by Hystrix.");
    }

    @Override
    public void load() {
        log.info("Initialised circuit breaker implemented by Hystrix.");
    }
}
