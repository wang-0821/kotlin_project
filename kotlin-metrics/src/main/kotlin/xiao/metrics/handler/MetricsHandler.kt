package xiao.metrics.handler

import xiao.metrics.MetricsEvent
import xiao.metrics.MetricsSummary

/**
 *
 * @author lix wang
 */
interface MetricsHandler {
    fun handle(
        oldSummary: Map<MetricsEvent, MetricsSummary>,
        newSummary: Map<MetricsEvent, MetricsSummary>
    )
}