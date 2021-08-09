package com.xiao.base.io

import com.xiao.base.util.UnsafeUtils
import sun.misc.Unsafe

/**
 *
 * @author lix wang
 */
class UnsafeDirectIntArray(capacity: Int) : UnsafeDirectArray<Int>(capacity, Unsafe.ARRAY_INT_INDEX_SCALE) {
    override fun get(index: Int): Int {
        check(index < capacity)
        return UnsafeUtils.UNSAFE.getInt(address + getOffset(index))
    }

    override fun set(index: Int, value: Int) {
        check(index < capacity)
        UnsafeUtils.UNSAFE.putInt(address + getOffset(index), value)
    }

    fun writeTo(dest: IntArray, destIndex: Int, srcIndex: Int, len: Int) {
        check(srcIndex + len <= capacity)
        UnsafeUtils.UNSAFE.copyMemory(
            null,
            address + getOffset(srcIndex),
            dest,
            Unsafe.ARRAY_INT_BASE_OFFSET + getOffset(destIndex),
            getOffset(len)
        )
    }
}