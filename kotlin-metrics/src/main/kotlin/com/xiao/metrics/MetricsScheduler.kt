package com.xiao.metrics

import com.xiao.base.executor.ExecutorServiceFactory
import com.xiao.base.scheduler.CronScheduler
import com.xiao.base.scheduler.ScheduledTask
import com.xiao.metrics.handler.MetricsHandler

/**
 *
 * @author lix wang
 */
class MetricsScheduler(
    name: String,
    executorServiceFactory: ExecutorServiceFactory,
    private val metricsHandlers: List<MetricsHandler>
) : CronScheduler(
    name,
    executorServiceFactory.newScheduledExecutorService(name, 2)
) {
    @ScheduledTask(initial = 30, fixedDelay = 30)
    fun executeMetrics() {
        val oldSummary = MetricsUtils.metricsSummary()
        MetricsUtils.resetSummary()
        val newSummary = MetricsUtils.metricsSummary()

        metricsHandlers.forEach {
            it.handle(oldSummary, newSummary)
        }
    }
}