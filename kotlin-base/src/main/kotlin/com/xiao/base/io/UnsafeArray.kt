package com.xiao.base.io

import com.xiao.base.util.UnsafeUtils

/**
 *
 * @author lix wang
 */
abstract class UnsafeArray<T>(
    capacity: Int,
    private val elementBytes: Int
) : AutoCloseable {
    val address = UnsafeUtils.UNSAFE.allocateMemory(getOffset(capacity))

    abstract fun get(index: Int): T

    abstract fun set(index: Int, value: T)

    override fun close() {
        UnsafeUtils.UNSAFE.freeMemory(address)
    }

    protected fun getOffset(index: Int): Long {
        return index.toLong() * elementBytes
    }
}