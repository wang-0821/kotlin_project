package xiao.metrics.handler

import xiao.metrics.MetricsEvent
import xiao.metrics.MetricsSummary
import java.util.concurrent.atomic.AtomicInteger

/**
 *
 * @author lix wang
 */
class MetricsCountHandler(private val counter: AtomicInteger) : MetricsHandler {
    override fun handle(
        oldSummary: Map<MetricsEvent, MetricsSummary>,
        newSummary: Map<MetricsEvent, MetricsSummary>
    ) {
        val current = counter.get()
        val increment = newSummary.entries.sumOf {
            it.value.times - (oldSummary[it.key]?.times ?: 0)
        }
        counter.set(current + increment)
    }
}