package com.xiao.base.lock

import com.xiao.base.util.UnsafeUtils

/**
 *
 * @author lix wang
 */
abstract class SegmentLock(protected val size: Int) {
    private val segments = IntArray(size)

    abstract fun lock(): Int

    open fun unlock(segmentId: Int) {
        casStateAt(segmentId, INUSE, UNUSE)
    }

    fun <T> use(func: (Int) -> T): T {
        val segmentId = lock()
        try {
            return func(segmentId)
        } finally {
            unlock(segmentId)
        }
    }

    protected fun casStateAt(index: Int, oldVal: Int, newVal: Int): Boolean {
        return UnsafeUtils.UNSAFE.compareAndSwapInt(segments, (index.toLong() shl ASHIFT) + ABASE, oldVal, newVal)
    }

    companion object {
        private val SEGMENT_LOCK_TABLE = IntArray::class.java
        private val ABASE = UnsafeUtils.UNSAFE.arrayBaseOffset(SEGMENT_LOCK_TABLE)
        private val ASHIFT = 31 - Integer.numberOfLeadingZeros(UnsafeUtils.UNSAFE.arrayIndexScale(SEGMENT_LOCK_TABLE))

        const val UNUSE = 0
        const val INUSE = 1
    }
}