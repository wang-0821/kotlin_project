package com.xiao.metrics.handler

import com.xiao.metrics.MetricsEvent
import com.xiao.metrics.MetricsSummary

/**
 *
 * @author lix wang
 */
interface MetricsHandler {
    fun handle(oldSummary: Map<MetricsEvent, MetricsSummary>, newSummary: Map<MetricsEvent, MetricsSummary>)
}