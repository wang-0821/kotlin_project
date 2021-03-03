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
        val oldSummary = mutableMapOf<MetricsEvent, MetricsSummary>()
        val newSummary = mutableMapOf<MetricsEvent, MetricsSummary>()
        for (metricsEvent in MetricsUrils.metricsContainerMap.keys()) {
            MetricsUrils.metricsSummaryMap[metricsEvent]
                ?.let {
                    oldSummary[metricsEvent] = it
                }
            MetricsUrils.resetMetricsSummary(metricsEvent)
                ?.let {
                    newSummary[metricsEvent] = it
                }
        }

        metricsHandlers.forEach {
            it.handle(oldSummary, newSummary)
        }
    }
}