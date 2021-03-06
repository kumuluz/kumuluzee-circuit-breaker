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
package com.netflix.hystrix;

import com.kumuluz.ee.fault.tolerance.commands.FallbackHelper;
import com.kumuluz.ee.fault.tolerance.commands.HystrixCommandConfiguration;
import com.kumuluz.ee.fault.tolerance.commands.SuccessThresholdCircuitBreaker;
import com.kumuluz.ee.fault.tolerance.metrics.BulkheadMetricsCollection;
import com.kumuluz.ee.fault.tolerance.models.ExecutionMetadata;
import com.netflix.config.ConfigurationManager;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import com.netflix.hystrix.strategy.properties.HystrixPropertiesFactory;
import org.eclipse.microprofile.faulttolerance.exceptions.BulkheadException;
import org.eclipse.microprofile.faulttolerance.exceptions.FaultToleranceException;
import org.jboss.weld.context.RequestContext;

import javax.interceptor.InvocationContext;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

/**
 * Hystrix generic command for wrapping method execution in fault tolerance.
 *
 * Temporarily moved to this package until Hystrix adds support for custom circuit breaker implementations.
 *
 * @author Luka Šarc
 * @since 1.0.0
 */
public class KumuluzHystrixGenericCommand extends HystrixCommand<Object> {

    private static final Logger log = Logger.getLogger(KumuluzHystrixGenericCommand.class.getName());

    private final InvocationContext invocationContext;
    private final RequestContext requestContext;
    private final ExecutionMetadata metadata;

    private final BulkheadMetricsCollection bulkheadMetricsCollection;
    private Instant waitingStartTime;

    private boolean threadExecution = false;

    public KumuluzHystrixGenericCommand(HystrixCommandConfiguration configuration, InvocationContext invocationContext,
                                        RequestContext requestContext, ExecutionMetadata metadata) {

        super(configuration.getGroupKey(), configuration.getCommandKey(), configuration.getThreadPoolKey(),
                SuccessThresholdCircuitBreaker.CustomCbFactory.getInstance(configuration.getCommandKey(),
                        configuration.getGroupKey(),
                        HystrixPropertiesFactory.getCommandProperties(configuration.getCommandKey(), null),
                        HystrixCommandMetrics.getInstance(configuration.getCommandKey(), configuration.getGroupKey(),
                                configuration.getThreadPoolKey(),
                                HystrixPropertiesFactory.getCommandProperties(configuration.getCommandKey(),
                                        null)),
                        metadata,
                        metadata.getCbMetricsCollection(invocationContext.getMethod().getName()).orElse(null)),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null);

        this.invocationContext = invocationContext;
        this.requestContext = requestContext;
        this.metadata = metadata;

        this.bulkheadMetricsCollection = metadata.getBulkheadMetricsCollection(invocationContext.getMethod().getName())
                .orElse(null);
    }

    @Override
    protected Object run() throws Exception {

        log.finest("Executing command '" + metadata.getCommandKey() + "'.");

        AtomicLong currentlyExecuting = null;
        if (this.bulkheadMetricsCollection != null) {
            bulkheadMetricsCollection.getCallsAccepted().inc();

            currentlyExecuting = bulkheadMetricsCollection.getCurrentlyExecuting();
            currentlyExecuting.incrementAndGet();

            if (metadata.isAsynchronous()) {
                bulkheadMetricsCollection.getCurrentlyWaiting().decrementAndGet();
                bulkheadMetricsCollection.getWaitingDuration()
                        .update(Duration.between(this.waitingStartTime, Instant.now()).toNanos());
            }
        }

        Object result;
        Object property = ConfigurationManager.getConfigInstance()
                .getProperty("hystrix.command." + metadata.getCommandKey() + ".execution.isolation.strategy");

        boolean requestContextActivated = false;
        threadExecution = property == null || property == HystrixCommandProperties.ExecutionIsolationStrategy.THREAD;

        Instant startTime = null;
        Instant endTime = null;
        try {
            if (threadExecution && !requestContext.isActive()) {
                requestContext.activate();
                requestContextActivated = true;
            }

            startTime = Instant.now();
            result = invocationContext.proceed();
            endTime = Instant.now();
        } catch (Throwable e) {
            if (isFallbackInvokeable(e) || e instanceof BulkheadException)
                throw e;

            throw new HystrixBadRequestException(e.getMessage(), e);
        } finally {
            if (requestContextActivated && requestContext.isActive())
                requestContext.deactivate();

            if (currentlyExecuting != null) {
                currentlyExecuting.decrementAndGet();
            }
            if (bulkheadMetricsCollection != null && startTime != null && endTime != null) {
                bulkheadMetricsCollection.getExecutionDuration().update(Duration.between(startTime, endTime).toNanos());
            }
        }

        return result;
    }

    @Override
    protected Object getFallback() {

        log.finest("Executing fallback for command '" + metadata.getCommandKey() + "'.");

        Exception executionException = getExceptionFromThrowable(getExecutionException());

        try {
            return FallbackHelper.executeFallback(executionException, metadata, invocationContext,
                    threadExecution ? requestContext : null);
        } catch (Exception e) {
            if (e instanceof FaultToleranceException) {
                throw (FaultToleranceException) e;
            }
            throw new FaultToleranceException(e);
        }
    }

    @Override
    public Future<Object> queue() {
        if (this.metadata.isAsynchronous() && this.bulkheadMetricsCollection != null) {
            this.bulkheadMetricsCollection.getCurrentlyWaiting().incrementAndGet();
            this.waitingStartTime = Instant.now();
        }
        return super.queue();
    }

    private boolean isFallbackInvokeable(Throwable e) {

        if (metadata.getCircuitBreaker() == null) {
            return true;
        }

        Class<? extends Throwable>[] failOn = metadata.getCircuitBreaker().failOn();

        for (Class<? extends Throwable> fo : failOn) {
            if (fo.isInstance(e))
                return true;
        }

        return false;
    }

}
