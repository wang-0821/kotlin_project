package com.xiao.base.executor

import java.time.Duration
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

/**
 *
 * @author lix wang
 */
abstract class BaseScheduledExecutor(
    val name: String,
    private val scheduledExecutorService: ScheduledExecutorService
) : ExecutorMonitor {
    /**
     * Execute once when delay end.
     */
    open fun <T : Any?> schedule(
        delay: Duration, command: () -> T
    ): ScheduledFuture<T> {
        return scheduledExecutorService.schedule(
            command,
            delay.toNanos(),
            TimeUnit.NANOSECONDS
        )
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
            command,
            initialDelay.toNanos(),
            period.toNanos(),
            TimeUnit.NANOSECONDS
        ) as ScheduledFuture<Unit>
    }

    @Suppress("UNCHECKED_CAST")
    open fun scheduleWithFixedDelay(
        initialDelay: Duration,
        delay: Duration,
        command: () -> Unit
    ): ScheduledFuture<Unit> {
        return scheduledExecutorService.scheduleWithFixedDelay(
            command,
            initialDelay.toNanos(),
            delay.toNanos(),
            TimeUnit.NANOSECONDS
        ) as ScheduledFuture<Unit>
    }

    open fun shutdown() {
        scheduledExecutorService.shutdown()
    }

    open fun shutdownNow() {
        scheduledExecutorService.shutdownNow()
    }

    open fun isShutdown(): Boolean {
        return scheduledExecutorService.isShutdown
    }
}