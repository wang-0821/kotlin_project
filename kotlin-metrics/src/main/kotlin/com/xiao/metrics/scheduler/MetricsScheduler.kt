package com.xiao.metrics.scheduler

import com.xiao.base.executor.ExecutorServiceFactory
import com.xiao.base.scheduler.CronScheduler
import com.xiao.base.scheduler.ScheduledTask
import com.xiao.metrics.MetricsHandler

/**
 *
 * @author lix wang
 */
class MetricsScheduler(
    name: String,
    executorServiceFactory: ExecutorServiceFactory,
    private val metricsHandlers: List<MetricsHandler>
) : CronScheduler(name, executorServiceFactory.newScheduledExecutorService(name, 2)) {
    @ScheduledTask(initial = 30, fixedDelay = 30)
    fun executeMetrics() {
        // TODO handle metrics
    }
}