package com.xiao.metrics

import com.xiao.base.util.UnsafeUtils
import io.netty.buffer.PooledByteBufAllocator

/**
 *
 * @author lix wang
 */
class MetricsSummary(
    val metricsEvent: MetricsEvent
) {
    var times: Long = 0
    var min: Int = Int.MIN_VALUE
    var max: Int = Int.MAX_VALUE
    var lastUpdateTime: Long = 0
    var avg: Int = 0

    private var writeableBytes = MAX_CAPACITY
    private var latencies = PooledByteBufAllocator.DEFAULT.buffer(MIN_CAPACITY, MAX_CAPACITY)

    fun addLatency(runningTime: Int) {
        // add latency by cas
        for (i in 1..5) {
            val prev = writeableBytes
            val current = prev - LATENCY_BYTES
            if (current >= 0) {
                if (UNSAFE.compareAndSwapInt(this, WRITEABLE_BYTES, prev, current)) {
                    latencies.writeInt(runningTime)
                    break
                }
            } else {
                break
            }
        }

        // set last update time
        for (i in 1..5) {
            val prev = lastUpdateTime
            if (UNSAFE.compareAndSwapObject(this, LAST_UPDATE_TIME, prev, System.currentTimeMillis())) {
                break
            }
        }
    }

    companion object {
        private const val MIN_CAPACITY = 256
        private const val MAX_CAPACITY = 8192
        private const val LATENCY_BYTES = 4

        private val UNSAFE = UnsafeUtils.getUnsafe()
        private val CLASS = MetricsSummary::class.java
        private val LAST_UPDATE_TIME = UNSAFE.objectFieldOffset(CLASS.getDeclaredField("lastUpdateTime"))
        private val WRITEABLE_BYTES = UNSAFE.objectFieldOffset(CLASS.getDeclaredField("writeableBytes"))
    }
}