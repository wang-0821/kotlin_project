package com.xiao.metrics.handler

import com.xiao.base.logging.KtLogger
import com.xiao.base.logging.LoggerType
import com.xiao.base.logging.Logging
import com.xiao.metrics.MetricsEvent
import com.xiao.metrics.MetricsSummary
import com.xiao.metrics.MetricsType

/**
 *
 * @author lix wang
 */
class ApiSlowMetricsHandler : MetricsHandler {
    override fun handle(
        oldSummary: Map<MetricsEvent, MetricsSummary>,
        newSummary: Map<MetricsEvent, MetricsSummary>
    ) {
        val apiSlowEvents: List<MetricsEvent> = newSummary.keys.filter { it.type == MetricsType.API_SLOW }
        if (apiSlowEvents.isNotEmpty()) {
            log.warn("Api slow: ${apiSlowEvents.joinToString { it.name() }}.")
        }
    }

    @KtLogger(LoggerType.METRICS)
    companion object : Logging()
}