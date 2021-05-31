package com.xiao.base.scheduler

import com.xiao.base.executor.AbstractExecutorService
import com.xiao.base.executor.CompletableCallback
import com.xiao.base.executor.ExecutorMonitor
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
abstract class AbstractScheduler(
    val name: String,
    private val scheduledExecutorService: ScheduledExecutorService
) : AbstractExecutorService(scheduledExecutorService), ExecutorMonitor {
    /**
     * Execute once when delay end.
     */
    open fun schedule(
        delay: Duration,
        command: () -> Unit
    ): SafeScheduledFuture<Unit> {
        val safeScheduledFuture = SafeScheduledFuture<Unit>()
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

    override fun taskCount(): Int {
        return -1
    }

    override fun taskCapacity(): Int {
        return -1
    }

    @Suppress("UNCHECKED_CAST")
    private fun toRunnable(
        command: () -> Unit,
        future: CompletableFuture<*>
    ): Runnable {
        return Runnable {
            CompletableCallback(command, future as CompletableFuture<Any?>).run()
        }
    }
}