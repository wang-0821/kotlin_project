package com.xiao.metrics

import java.util.concurrent.ConcurrentHashMap

/**
 *
 * @author lix wang
 */
object MetricsHelper {
    @Volatile
    private var metricsSummaryMap = ConcurrentHashMap<MetricsEvent, MetricsSummary>()

    @JvmStatic
    fun recordMetrics(event: MetricsEvent, runningMills: Int) {
        // atomic ops
        metricsSummaryMap.putIfAbsent(event, MetricsSummary(event))
        metricsSummaryMap[event]?.addLatency(runningMills)
    }
}