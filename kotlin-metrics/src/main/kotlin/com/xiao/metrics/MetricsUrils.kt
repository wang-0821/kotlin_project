package com.xiao.metrics

import com.xiao.base.thread.ThreadSafe
import com.xiao.base.thread.ThreadUnsafe
import java.util.concurrent.ConcurrentHashMap

/**
 *
 * @author lix wang
 */
object MetricsUrils {
    var metricsContainerMap = ConcurrentHashMap<MetricsEvent, MetricsContainer>()
        private set
    var metricsSummaryMap = ConcurrentHashMap<MetricsEvent, MetricsSummary>()
        private set

    @ThreadSafe
    @JvmStatic
    fun recordMetrics(event: MetricsEvent, runningMills: Int): Boolean {
        // atomic ops
        metricsContainerMap.putIfAbsent(event, MetricsContainer())
        return metricsContainerMap[event]?.addLatency(runningMills) ?: false
    }

    /**
     * Start calculate and update metrics summary.
     */
    @ThreadUnsafe
    @JvmStatic
    fun resetMetricsSummary(event: MetricsEvent): MetricsSummary? {
        val latencies = metricsContainerMap[event]?.resetLatencies()
        if (latencies.isNullOrEmpty()) {
            return null
        }
        val metricsSummary = MetricsSummary()
            .apply {
                calculateSummary(latencies)
            }
        metricsSummaryMap[event] = metricsSummary
        return metricsSummary
    }
}