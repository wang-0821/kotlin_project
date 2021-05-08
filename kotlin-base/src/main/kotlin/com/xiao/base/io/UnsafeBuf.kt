package com.xiao.base.io

import com.xiao.base.util.UnsafeUtils

/**
 *
 * @author lix wang
 */
abstract class UnsafeBuf<T>(
    capacity: Int,
    private val elementBytes: Int
) : AutoCloseable {
    val address = UnsafeUtils.UNSAFE.allocateMemory(getOffset(capacity))

    abstract fun get(index: Int): T

    abstract fun set(index: Int, value: T)

    protected fun copy(srcAddress: Long, destAddress: Long, length: Int) {
        UnsafeUtils.UNSAFE.copyMemory(srcAddress, destAddress, getOffset(length))
    }

    protected fun copy(src: UnsafeBuf<T>, srcIndex: Int, descIndex: Int, length: Int) {
        UnsafeUtils.UNSAFE.copyMemory(
            src.address + getOffset(srcIndex),
            address + getOffset(descIndex),
            getOffset(length)
        )
    }

    override fun close() {
        UnsafeUtils.UNSAFE.freeMemory(address)
    }

    protected fun getOffset(index: Int): Long {
        return index.toLong() * elementBytes
    }
}