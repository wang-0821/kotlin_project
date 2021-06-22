package com.xiao.metrics

import com.xiao.base.thread.ThreadSafe
import com.xiao.base.thread.ThreadUnsafe
import java.util.concurrent.ConcurrentHashMap

/**
 *
 * @author lix wang
 */
object MetricsUtils {
    const val STATE_SUCCESS = "success"
    const val STATE_FAIL = "fail"
    const val STATE_SLOW = "slow"

    const val TYPE_API = "API"
    const val TYPE_DB = "DB"
    const val TYPE_RPC = "RPC"

    private const val LEGACY_LAST_MILLS = 30 * 60 * 1000
    private val metricsBufferMap = ConcurrentHashMap<MetricsEvent, MetricsBuffer>()
    private val metricsSummaryMap = ConcurrentHashMap<MetricsEvent, MetricsSummary>()

    @ThreadSafe
    @JvmStatic
    fun recordMetrics(
        type: String,
        state: String,
        mills: Int,
        prefix: String?,
        suffix: String?
    ): Boolean {
        val event = MetricsEvent(type, state, prefix, suffix)
        metricsBufferMap.putIfAbsent(event, MetricsBuffer())
        return metricsBufferMap[event]!!.add(mills)
    }

    @ThreadUnsafe
    @JvmStatic
    fun metricsSummary(): Map<MetricsEvent, MetricsSummary> {
        return metricsSummaryMap.entries
            .associate {
                it.key to it.value
            }.also {
                cleanLegacyMetricsBuffers()
            }
    }

    @ThreadUnsafe
    @JvmStatic
    fun updateSummary() {
        cleanLegacyMetricsBuffers()

        val bufferMap = mutableMapOf<MetricsEvent, MetricsBuffer>()
        metricsBufferMap.forEach { (metricsEvent, metricsBuffer) ->
            bufferMap[metricsEvent] = metricsBuffer
            metricsBufferMap[metricsEvent] = metricsBuffer.newBuffer()
        }

        bufferMap.forEach { (metricsEvent, metricsBuffer) ->
            metricsBuffer.use {
                var buffer = metricsSummaryMap[metricsEvent]
                if (buffer == null) {
                    buffer = MetricsSummary()
                    metricsSummaryMap[metricsEvent] = buffer
                }
                buffer.update(metricsBuffer.toList())
            }
        }
    }

    private fun cleanLegacyMetricsBuffers() {
        metricsBufferMap.forEach { (event, buffer) ->
            if (System.currentTimeMillis() - buffer.lastUpdateTime > LEGACY_LAST_MILLS) {
                metricsBufferMap.remove(event)
                buffer.close()
            }
        }
    }
}