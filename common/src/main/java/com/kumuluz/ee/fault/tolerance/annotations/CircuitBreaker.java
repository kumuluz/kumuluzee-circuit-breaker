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
package com.kumuluz.ee.fault.tolerance.annotations;

import javax.enterprise.util.Nonbinding;
import javax.interceptor.InterceptorBinding;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.time.temporal.ChronoUnit;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation applying circuit breker pattern to either method
 * or class
 *
 * @author Luka Šarc
 */
@Inherited
@InterceptorBinding
@Retention(RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface CircuitBreaker {

    @Nonbinding Class<? extends Throwable>[] failOn() default {Throwable.class};

    @Nonbinding int delay() default -1;

    @Nonbinding ChronoUnit delayUnit() default ChronoUnit.MILLIS;

    @Nonbinding int requestVolumeThreshold() default 20;

    @Nonbinding double failureRatio() default .50;

    @Nonbinding int successThreshold() default 1;

}