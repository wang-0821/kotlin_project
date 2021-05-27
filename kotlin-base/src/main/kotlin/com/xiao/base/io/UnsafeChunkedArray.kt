package com.xiao.base.io

import com.xiao.base.CommonConstants

/**
 *
 * @author lix wang
 */
abstract class UnsafeChunkedArray<T, E : UnsafeArray<T>>(
    private val capacity: Int = Int.MAX_VALUE,
    protected val chunkCapacity: Int = CommonConstants.KILO_BUFFER_SIZE,
    private val allocator: UnsafeArrayAllocator<E>
) : AutoCloseable {
    private var readIndex: Int = 0
    protected var writeIndex: Int = 0
    protected val chunks = mutableListOf<E>()

    abstract fun toList(): List<T>

    open fun add(value: T) {
        if (writeIndex >= capacity) {
            throw IllegalStateException("Array is out of capacity.")
        }

        if (writeIndex >= chunks.size * chunkCapacity) {
            chunks.add(allocator.allocate(chunkCapacity))
        }

        set(writeIndex++, value)
    }

    fun read(): T {
        check(isReadable())
        return get(readIndex++)
    }

    fun get(index: Int): T {
        check(index < this.writeIndex && index >= 0)
        return chunks[chunkIndex(index)].get(offset(index))
    }

    fun set(index: Int, value: T) {
        check(index < this.writeIndex && index >= 0)
        chunks[chunkIndex(index)].set(offset(index), value)
    }

    fun size(): Int {
        return writeIndex
    }

    fun isWriteable(): Boolean {
        return writeIndex < capacity
    }

    fun isReadable(): Boolean {
        return readIndex < writeIndex
    }

    fun resetReadIndex() {
        readIndex = 0
    }

    fun resetWriteIndex() {
        writeIndex = 0
    }

    override fun close() {
        chunks.forEach {
            it.close()
        }
    }

    protected fun chunkIndex(index: Int): Int {
        return index / chunkCapacity
    }

    private fun offset(index: Int): Int {
        return index % chunkCapacity
    }
}