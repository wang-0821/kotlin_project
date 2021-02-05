package com.xiao.base.executor

import java.time.Duration
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

/**
 *
 * @author lix wang
 */
@Suppress("unused")
abstract class BaseScheduledExecutor(
    val name: String,
    private val scheduledExecutorService: ScheduledExecutorService
) : ExecutorMonitor {
    /**
     * Execute once when delay end.
     */
    open fun <T : Any?> schedule(
        delay: Duration,
        command: () -> T
    ): SafeScheduledFuture<T> {
        val safeScheduledFuture = SafeScheduledFuture<T>()
        val future = scheduledExecutorService.schedule(
            toRunnable(command, safeScheduledFuture),
            delay.toNanos(),
            TimeUnit.NANOSECONDS
        )

        return safeScheduledFuture
            .apply {
                putFuture(future)
            }
    }

    /**
     * Execute first after initialD
     */
    @Suppress("UNCHECKED_CAST")
    open fun scheduleAtFixedRate(
        initialDelay: Duration,
        period: Duration,
        command: () -> Unit
    ): SafeScheduledFuture<Unit> {
        val safeScheduledFuture = SafeScheduledFuture<Unit>()
        val future = scheduledExecutorService.scheduleAtFixedRate(
            toRunnable(command, safeScheduledFuture),
            initialDelay.toNanos(),
            period.toNanos(),
            TimeUnit.NANOSECONDS
        ) as ScheduledFuture<Unit>

        return safeScheduledFuture
            .apply {
                putFuture(future)
            }
    }

    @Suppress("UNCHECKED_CAST")
    open fun scheduleWithFixedDelay(
        initialDelay: Duration,
        delay: Duration,
        command: () -> Unit
    ): SafeScheduledFuture<Unit> {
        val safeScheduledFuture = SafeScheduledFuture<Unit>()
        val future = scheduledExecutorService.scheduleWithFixedDelay(
            toRunnable(command, safeScheduledFuture),
            initialDelay.toNanos(),
            delay.toNanos(),
            TimeUnit.NANOSECONDS
        ) as ScheduledFuture<Unit>

        return safeScheduledFuture
            .apply {
                putFuture(future)
            }
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

    override fun taskCount(): Int {
        return -1
    }

    override fun taskCapacity(): Int {
        return -1
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : Any?> toRunnable(
        command: () -> T,
        future: CompletableFuture<T>
    ): Runnable {
        return CompletableCallback(command, future as CompletableFuture<Any?>, null)
    }
}