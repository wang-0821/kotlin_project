package com.xiao.metrics.handler

import com.xiao.base.logging.KtLogger
import com.xiao.base.logging.LoggerType
import com.xiao.base.logging.Logging
import com.xiao.metrics.MetricsEvent
import com.xiao.metrics.MetricsSummary
import com.xiao.metrics.MetricsUtils

/**
 *
 * @author lix wang
 */
class ApiSlowMetricsHandler : MetricsHandler {
    override fun handle(
        oldSummary: Map<MetricsEvent, MetricsSummary>,
        newSummary: Map<MetricsEvent, MetricsSummary>
    ) {
        newSummary.keys.asSequence()
            .filter {
                it.type == MetricsUtils.TYPE_API && it.state == MetricsUtils.STATE_SLOW
            }.toSet()
            .forEach {
                log.warn("Api slow: ${it.name}.")
            }
    }

    @KtLogger(LoggerType.METRICS)
    companion object : Logging()
}