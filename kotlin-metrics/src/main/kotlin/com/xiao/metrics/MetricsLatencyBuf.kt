package com.xiao.metrics

import com.xiao.base.thread.ThreadUnsafe
import io.netty.buffer.ByteBuf
import io.netty.buffer.PooledByteBufAllocator
import io.netty.util.ReferenceCountUtil

/**
 *
 * @author lix wang
 */
@ThreadUnsafe
class MetricsLatencyBuf(
    private val minCapacity: Int = 64,
    private val maxCapacity: Int = 2048
) {
    var times: Long = 0
        private set
    private var lastUpdateTime: Long = -1
    private var writeableSize = maxCapacity
    private var latencies: ByteBuf? = null

    fun writeable(): Boolean {
        return writeableSize > 0
    }

    fun addLatency(runningTime: Int) {
        if (writeable()) {
            writeableSize--
            if (latencies == null) {
                latencies = PooledByteBufAllocator.DEFAULT.buffer(
                    minCapacity * LATENCY_BYTES, maxCapacity * LATENCY_BYTES
                )
            }
            latencies!!.writeInt(runningTime)
            times++
            lastUpdateTime = System.currentTimeMillis()
        }
    }

    fun resetLatencies(target: MutableList<Int>) {
        latencies?.let {
            try {
                while (it.isReadable(LATENCY_BYTES)) {
                    target.add(it.readInt())
                }
            } finally {
                ReferenceCountUtil.release(it)
                writeableSize = maxCapacity
                latencies = null
            }
        }
    }

    companion object {
        private const val LATENCY_BYTES = 4
    }
}