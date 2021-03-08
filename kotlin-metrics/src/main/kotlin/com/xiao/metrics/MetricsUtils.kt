package com.xiao.metrics

import com.xiao.base.thread.ThreadSafe
import com.xiao.base.thread.ThreadUnsafe
import java.util.concurrent.ConcurrentHashMap

/**
 *
 * @author lix wang
 */
object MetricsUtils {
    @Volatile private var metricsContainerMap = ConcurrentHashMap<MetricsEvent, MetricsContainer>()
    @Volatile private var metricsSummaryMap = ConcurrentHashMap<MetricsEvent, MetricsSummary>()

    @ThreadSafe
    @JvmStatic
    fun recordMetrics(event: MetricsEvent, runningMills: Int): Boolean {
        // atomic ops
        metricsContainerMap.putIfAbsent(event, MetricsContainer())
        return metricsContainerMap[event]?.addLatency(runningMills) ?: false
    }

    @ThreadUnsafe
    @JvmStatic
    fun metricsSummary(): Map<MetricsEvent, MetricsSummary> {
        return metricsSummaryMap.entries
            .associate {
                it.key to it.value
            }
    }

    @ThreadUnsafe
    @JvmStatic
    fun resetSummary() {
        for (event in metricsContainerMap.keys) {
            val latencies = metricsContainerMap[event]?.resetLatencies()
            if (latencies.isNullOrEmpty()) {
                metricsSummaryMap.remove(event)
                continue
            }

            val metricsSummary = MetricsSummary()
                .apply {
                    calculateSummary(latencies)
                }
            metricsSummaryMap[event] = metricsSummary
        }
    }
}