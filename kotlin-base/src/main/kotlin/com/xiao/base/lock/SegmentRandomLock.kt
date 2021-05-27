package com.xiao.base.lock

import kotlin.random.Random

/**
 *
 * @author lix wang
 */
class SegmentRandomLock(size: Int) : SegmentLock(size) {
    private var nextIndex = 0

    override fun lock(): Int {
        Random.nextInt()
        var index = nextIndex % size
        while (true) {
            while (index < size) {
                if (casStateAt(index, UNUSE, INUSE)) {
                    nextIndex = (index + 1) % size
                    return index
                }
                index++
            }
            index = 0
        }
    }
}