package com.xiao.metrics

/**
 *
 * @author lix wang
 */
interface MetricsHandler {
    fun handle(metricsSummary: MetricsSummary)
}