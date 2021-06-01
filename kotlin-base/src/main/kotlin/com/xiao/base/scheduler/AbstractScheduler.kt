package com.xiao.base.scheduler

import com.xiao.base.executor.AbstractExecutorService
import java.time.Duration
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

/**
 *
 * @author lix wang
 */
@Suppress("unused")
abstract class AbstractScheduler(
    val name: String,
    val scheduledExecutorService: ScheduledExecutorService
) : AbstractExecutorService(scheduledExecutorService) {
    /**
     * Execute once when delay end.
     */
    @Suppress("UNCHECKED_CAST")
    open fun schedule(
        delay: Duration,
        command: () -> Unit
    ): ScheduledFuture<Unit> {
        return scheduledExecutorService.schedule(
            { command() },
            delay.toMillis(),
            TimeUnit.MILLISECONDS
        ) as ScheduledFuture<Unit>
    }

    /**
     * Execute first after initialD
     */
    @Suppress("UNCHECKED_CAST")
    open fun scheduleAtFixedRate(
        initialDelay: Duration,
        period: Duration,
        command: () -> Unit
    ): ScheduledFuture<Unit> {
        return scheduledExecutorService.scheduleAtFixedRate(
            { command() },
            initialDelay.toNanos(),
            period.toMillis(),
            TimeUnit.MILLISECONDS
        ) as ScheduledFuture<Unit>
    }

    @Suppress("UNCHECKED_CAST")
    open fun scheduleWithFixedDelay(
        initialDelay: Duration,
        delay: Duration,
        command: () -> Unit
    ): ScheduledFuture<Unit> {
        return scheduledExecutorService.scheduleWithFixedDelay(
            { command() },
            initialDelay.toNanos(),
            delay.toMillis(),
            TimeUnit.MILLISECONDS
        ) as ScheduledFuture<Unit>
    }
}