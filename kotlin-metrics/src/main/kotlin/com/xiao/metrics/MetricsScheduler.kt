package com.xiao.metrics

import com.xiao.base.executor.ExecutorServiceFactory
import com.xiao.base.scheduler.CronScheduler
import com.xiao.base.scheduler.ScheduledTask
import com.xiao.metrics.handler.MetricsHandler
import java.util.concurrent.TimeUnit

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
    executorServiceFactory.newScheduledExecutorService(name, 1)
) {
    @ScheduledTask(initial = 5, fixedDelay = 30, timeUnit = TimeUnit.SECONDS)
    fun executeMetrics() {
        val oldSummary = MetricsUtils.metricsSummary()
        MetricsUtils.updateSummary()
        val newSummary = MetricsUtils.metricsSummary()

        metricsHandlers.forEach {
            it.handle(oldSummary, newSummary)
        }
    }
}