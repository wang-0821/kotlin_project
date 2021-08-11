package xiao.metrics.handler

import xiao.base.logging.KtLogger
import xiao.base.logging.LoggerType
import xiao.base.logging.Logging
import xiao.metrics.MetricsEvent
import xiao.metrics.MetricsSummary
import xiao.metrics.MetricsUtils

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