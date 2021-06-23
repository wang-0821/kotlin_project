package com.xiao.metrics

import com.xiao.base.io.UnsafeChunkedIntArray
import com.xiao.base.lock.SpinLock
import kotlin.math.max
import kotlin.math.min

/**
 *
 * @author lix wang
 */
class MetricsBuffer(
    private val capacity: Int = DEFAULT_CAPACITY
) : AutoCloseable {
    private var buffer = UnsafeChunkedIntArray(MAX_TOTAL_CAPACITY, capacity)
    private val lock = SpinLock()
    var lastUpdateTime: Long = System.currentTimeMillis()
        private set

    fun add(mills: Int): Boolean {
        return lock.use {
            if (buffer.isWriteable()) {
                buffer.add(mills)
                lastUpdateTime = System.currentTimeMillis()
                true
            } else {
                false
            }
        }
    }

    fun newBuffer(): MetricsBuffer {
        val newCapacity = when {
            buffer.size() < capacity - MIN_CAPACITY -> {
                max(capacity - MIN_CAPACITY, MIN_CAPACITY)
            }
            buffer.size() > capacity + MIN_CAPACITY -> {
                min(MAX_TOTAL_CAPACITY, capacity + MIN_CAPACITY)
            }
            else -> {
                capacity
            }
        }

        return MetricsBuffer(newCapacity)
    }

    fun toList(): List<Int> {
        val totalSize = buffer.size()
        val list = MutableList(totalSize) { 0 }
        var index = 0
        while (buffer.isReadable()) {
            list[index++] = buffer.read()
        }
        return list
    }

    override fun close() {
        lock.use {
            buffer.close()
        }
    }

    companion object {
        const val MIN_CAPACITY = 128
        const val DEFAULT_CAPACITY = 512
        const val MAX_TOTAL_CAPACITY = 8192
    }
}