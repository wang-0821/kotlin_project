package com.xiao.base.io

import com.xiao.base.CommonConstants
import kotlin.math.min

/**
 *
 * @author lix wang
 */
class UnsafeChunkedIntArray(
    capacity: Int = Int.MAX_VALUE,
    chunkCapacity: Int = CommonConstants.KILO_BUFFER_SIZE,
    allocator: UnsafeArrayAllocator<UnsafeIntArray> = defaultUnsafeIntArrayAllocator
) : UnsafeChunkedArray<Int, UnsafeIntArray>(capacity, chunkCapacity, allocator) {
    override fun toList(): List<Int> {
        return toArray().toList()
    }

    fun toArray(): IntArray {
        var fromIndex = 0
        val toIndex = writeIndex
        val intArray = IntArray(toIndex - fromIndex)
        while (fromIndex < toIndex) {
            val chunkIndex = chunkIndex(fromIndex)
            val readSize = min(chunkCapacity, toIndex - fromIndex)
            chunks[chunkIndex].writeTo(intArray, fromIndex, 0, readSize)
            fromIndex += readSize
        }
        return intArray
    }

    companion object {
        val defaultUnsafeIntArrayAllocator = object : UnsafeArrayAllocator<UnsafeIntArray> {
            override fun allocate(capacity: Int): UnsafeIntArray {
                return UnsafeIntArray(capacity)
            }
        }
    }
}