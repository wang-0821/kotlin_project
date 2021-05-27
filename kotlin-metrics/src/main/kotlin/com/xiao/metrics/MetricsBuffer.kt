package com.xiao.metrics

import com.xiao.base.io.UnsafeChunkedIntArray
import com.xiao.base.lock.SegmentRandomLock
import kotlin.math.max
import kotlin.math.min

/**
 *
 * @author lix wang
 */
class MetricsBuffer(
    private val size: Int = MAX_SIZE,
    private val capacity: Int = MAX_CAPACITY
) : AutoCloseable {
    private var buffers = Array(size) { UnsafeChunkedIntArray(MAX_TOTAL_CAPACITY, capacity) }
    private val lock = SegmentRandomLock(size)
    var lastUpdateTime: Long? = null
        private set

    fun add(mills: Int): Boolean {
        return lock.use { index ->
            if (buffers[index].isWriteable()) {
                buffers[index].add(mills)
                lastUpdateTime = System.currentTimeMillis()
                true
            } else {
                false
            }
        }
    }

    fun newBuffer(): MetricsBuffer {
        var highBufferCount = 0
        var lowBufferCount = 0
        val highCapacity = 0.85 * capacity
        val lowCapacity = 0.5 * capacity
        buffers.forEach { buffer ->
            when {
                buffer.size() > highCapacity -> {
                    highBufferCount++
                }
                buffer.size() < lowCapacity -> {
                    lowBufferCount++
                }
            }
        }
        var newSize = size - lowBufferCount / 2 + highBufferCount / 2
        var newCapacity = capacity
        if (newSize >= MAX_SIZE) {
            newSize = MAX_SIZE
            newCapacity = min(newCapacity + MIN_CAPACITY, MAX_CAPACITY)
        } else if (newSize <= MIN_SIZE) {
            newSize = MIN_SIZE
            newCapacity = max(newCapacity - MIN_CAPACITY, MIN_CAPACITY)
        }

        return MetricsBuffer(newSize, newCapacity)
    }

    fun toList(): List<Int> {
        val totalSize = buffers.sumOf { it.size() }
        val list = MutableList(totalSize) { 0 }
        buffers.forEach {
            it.resetReadIndex()
            while (it.isReadable()) {
                list.add(it.read())
            }
        }
        return list
    }

    override fun close() {
        buffers.forEach {
            it.close()
        }
    }

    companion object {
        const val MIN_CAPACITY = 128
        const val MAX_CAPACITY = 512
        const val MAX_TOTAL_CAPACITY = 4096
        const val MIN_SIZE = 1
        val MAX_SIZE = Runtime.getRuntime().availableProcessors()
    }
}