package com.xiao.base.io

import com.xiao.base.util.UnsafeUtils

/**
 *
 * @author lix wang
 */
abstract class UnsafeDirectArray<T>(
    capacity: Int,
    elementBytes: Int
) : DirectArray<T>(capacity, elementBytes) {
    val address = UnsafeUtils.UNSAFE.allocateMemory(getOffset(capacity))

    override fun close() {
        UnsafeUtils.UNSAFE.freeMemory(address)
    }
}