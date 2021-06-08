package com.xiao.base.lock

/**
 * Use [SegmentBalanceLock] can make each segment locked in rough balance.
 *
 * @author lix wang
 */
class SegmentBalanceLock(size: Int) : SegmentLock(size) {
    @Volatile private var nextIndex = 0

    override fun lock(): Int {
        var index = nextIndex
        while (true) {
            while (index < size) {
                if (casStateAt(index, UNUSE, INUSE)) {
                    nextIndex = (nextIndex + 1) % size
                    return index
                }
                index++
            }
            index = 0
        }
    }
}