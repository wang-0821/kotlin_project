package com.xiao.base.lock

/**
 *
 * @author lix wang
 */
class SegmentSpinLock(size: Int) : SegmentLock(size) {
    override fun lock(): Int {
        while (true) {
            var i = 0
            while (i < size) {
                if (casStateAt(i, UNUSE, INUSE)) {
                    return i
                }
                i++
            }
        }
    }
}