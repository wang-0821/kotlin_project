package com.xiao.base.io

/**
 *
 * @author lix wang
 */
interface UnsafeArrayAllocator<T : UnsafeArray<*>> {
    fun allocate(capacity: Int): T
}