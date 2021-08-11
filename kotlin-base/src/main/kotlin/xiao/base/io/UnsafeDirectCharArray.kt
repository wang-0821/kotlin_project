package xiao.base.io

import sun.misc.Unsafe
import xiao.base.util.UnsafeUtils

/**
 *
 * @author lix wang
 */
class UnsafeDirectCharArray(capacity: Int) : UnsafeDirectArray<Char>(capacity, Unsafe.ARRAY_CHAR_INDEX_SCALE) {
    override fun get(index: Int): Char {
        check(index < capacity)
        return UnsafeUtils.UNSAFE.getChar(address + getOffset(index))
    }

    override fun set(index: Int, value: Char) {
        check(index < capacity)
        UnsafeUtils.UNSAFE.putChar(address + getOffset(index), value)
    }

    fun readFrom(src: CharArray, srcIndex: Int, destIndex: Int, length: Int) {
        check(destIndex + length <= capacity)
        UnsafeUtils.UNSAFE.copyMemory(
            src,
            Unsafe.ARRAY_CHAR_BASE_OFFSET + getOffset(srcIndex),
            null,
            address + getOffset(destIndex),
            getOffset(length)
        )
    }

    fun writeTo(dest: CharArray, destIndex: Int, srcIndex: Int, length: Int) {
        check(srcIndex + length <= capacity)
        UnsafeUtils.UNSAFE.copyMemory(
            null,
            address + getOffset(srcIndex),
            dest,
            Unsafe.ARRAY_CHAR_BASE_OFFSET + getOffset(destIndex),
            getOffset(length)
        )
    }
}