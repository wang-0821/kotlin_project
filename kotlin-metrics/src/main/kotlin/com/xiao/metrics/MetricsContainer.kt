package com.xiao.metrics

import com.xiao.base.thread.ThreadSafe
import com.xiao.base.thread.ThreadUnsafe
import com.xiao.base.util.UnsafeUtils

/**
 *
 * @author lix wang
 */
class MetricsContainer(
    private val capacity: Int = 5
) {
    private val latencies = Array<MetricsLatencyBuf?>(capacity) { null }
    private val stateTable = IntArray(capacity) { EMPTY }

    /**
     * If busy or full, will drop the latency.
     */
    @ThreadSafe
    fun addLatency(mills: Int): Boolean {
        val index = getLatencyIndex()
        if (index >= 0) {
            var lockReleased = false
            try {
                val latency = latencies[index]!!
                return if (latency.writeable()) {
                    latency.addLatency(mills)
                    lockReleased = if (latency.writeable()) {
                        casStateAt(index, INUSE, READY)
                    } else {
                        casStateAt(index, INUSE, FULL)
                    }
                    true
                } else {
                    lockReleased = casStateAt(index, INUSE, FULL)
                    false
                }
            } finally {
                // make sure release lock
                if (!lockReleased) {
                    setStateAt(index, READY)
                }
            }
        }
        return false
    }

    @ThreadUnsafe
    fun resetLatencies(): List<Int> {
        val result = mutableListOf<Int>()
        val resetArray = BooleanArray(capacity)
        while (true) {
            var allRest = true
            for (i in stateTable.indices) {
                val state = stateAt(i)
                if (!resetArray[i]) {
                    if (state == READY || state == FULL) {
                        // legacy latency byteBuf
                        if (casStateAt(i, state, INUSE)) {
                            latencies[i]!!.resetLatencies(result)
                            resetArray[i] = true
                            setStateAt(i, READY)
                        }
                    }
                }
                if (state != EMPTY && !resetArray[i]) {
                    allRest = false
                }
            }
            if (allRest) {
                break
            }
        }
        return result
    }

    private fun getLatencyIndex(timeout: Long = 5000): Int {
        val timeoutMills = if (timeout > 0) {
            System.currentTimeMillis() + timeout
        } else {
            -1
        }
        while (true) {
            // timeout
            if (timeoutMills > 0 && System.currentTimeMillis() > timeoutMills) {
                return -1
            }

            // loop latencies
            var full = true
            for (i in stateTable.indices) {
                val state = stateAt(i)
                if (state < FULL) {
                    full = false
                }

                if (state < READY) {
                    // create a summary
                    if (casStateAt(i, EMPTY, INUSE)) {
                        val latency = MetricsLatencyBuf()
                        latencies[i] = latency
                        return i
                    }
                } else {
                    if (state == READY) {
                        if (casStateAt(i, READY, INUSE)) {
                            return i
                        }
                    }
                }
            }

            // all full
            if (full) {
                return -1
            }
        }
    }

    private fun stateAt(index: Int): Int {
        // index << ASHIFT + ABASE = index << 2 + ABASE = 4 * index + ABASE
        return UNSAFE.getIntVolatile(stateTable, (index.toLong() shl ASHIFT) + ABASE)
    }

    private fun casStateAt(index: Int, origin: Int, expect: Int): Boolean {
        return UNSAFE.compareAndSwapInt(stateTable, (index.toLong() shl ASHIFT) + ABASE, origin, expect)
    }

    private fun setStateAt(index: Int, value: Int) {
        return UNSAFE.putIntVolatile(stateTable, ((index.toLong()) shl ASHIFT) + ABASE, value)
    }

    companion object {
        private const val EMPTY = -1
        private const val READY = 0
        private const val INUSE = 1
        private const val FULL = 2

        private val UNSAFE = UnsafeUtils.getUnsafe()
        private val STATE_ARRAY_CLASS = IntArray::class.java
        // 起始位置，所有的基本类型、Object、String都是16，因为JVM开启指针压缩后，12字节的对象头 + 4字节(length)
        private val ABASE = UNSAFE.arrayBaseOffset(STATE_ARRAY_CLASS)
        // 元素类型为int，长度为4，二进制前面有29位0补位，此时ASHIFT = 31 - 29 = 2
        private val ASHIFT = 31 - Integer.numberOfLeadingZeros(UNSAFE.arrayIndexScale(STATE_ARRAY_CLASS))
    }
}