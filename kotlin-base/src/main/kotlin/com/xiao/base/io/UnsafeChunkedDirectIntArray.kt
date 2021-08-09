package com.xiao.base.io

import com.xiao.base.CommonConstants
import kotlin.math.min

/**
 *
 * @author lix wang
 */
class UnsafeChunkedDirectIntArray(
    capacity: Int = Int.MAX_VALUE,
    chunkCapacity: Int = CommonConstants.KILO_BUFFER_SIZE,
    allocatorDirectInt: UnsafeDirectArrayAllocator<UnsafeDirectIntArray> = defaultUnsafeIntArrayAllocator
) : UnsafeChunkedDirectArray<Int, UnsafeDirectIntArray>(capacity, chunkCapacity, allocatorDirectInt) {
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
        val defaultUnsafeIntArrayAllocator = object : UnsafeDirectArrayAllocator<UnsafeDirectIntArray> {
            override fun allocate(capacity: Int): UnsafeDirectIntArray {
                return UnsafeDirectIntArray(capacity)
            }
        }
    }
}