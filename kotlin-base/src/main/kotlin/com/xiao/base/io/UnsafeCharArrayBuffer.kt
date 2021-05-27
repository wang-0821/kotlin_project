package com.xiao.base.io

import java.nio.CharBuffer

/**
 *
 * @author lix wang
 */
class UnsafeCharArrayBuffer(private val capacity: Int) : AutoCloseable {
    private var index = 0
    private var unsafeCharArrayBuf: UnsafeCharArray? = null

    fun append(charBuffer: CharBuffer): Int {
        val charBufferRemaining = charBuffer.remaining()
        if (charBufferRemaining <= 0) {
            return 0
        }
        unsafeCharArrayBuf = unsafeCharArrayBuf ?: UnsafeCharArray(capacity)
        val currentRemaining = remaining()
        return if (charBufferRemaining <= currentRemaining) {
            unsafeCharArrayBuf!!.readFrom(charBuffer.array(), charBuffer.position(), index, charBufferRemaining)
            index += charBufferRemaining
            0
        } else {
            unsafeCharArrayBuf!!.readFrom(charBuffer.array(), charBuffer.position(), index, currentRemaining)
            charBuffer.position(charBuffer.position() + currentRemaining)
            index += currentRemaining
            charBuffer.remaining()
        }
    }

    fun writeTo(dest: CharArray, destIndex: Int, srcIndex: Int, length: Int) {
        unsafeCharArrayBuf?.writeTo(dest, destIndex, srcIndex, length)
    }

    fun size(): Int {
        return index
    }

    fun remaining(): Int {
        return capacity - index
    }

    override fun close() {
        unsafeCharArrayBuf?.close()
    }
}