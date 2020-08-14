package com.xiao.rpc.io

import com.xiao.rpc.helper.IoHelper
import java.nio.CharBuffer

/**
 *
 * @author lix wang
 */
class PooledBuffer {
    private val allocateSize: Int
    private val buffers = mutableListOf<PooledCharArrayBuffer>()
    private var currentBuffer: PooledCharArrayBuffer

    constructor(allocateSize: Int = IoHelper.BUFFER_SIZE) {
        this.allocateSize = allocateSize
        this.currentBuffer = PooledCharArrayBuffer(allocateSize)
        buffers.add(currentBuffer)
    }

    fun appendCharBuffer(charBuffer: CharBuffer) {
        while (true) {
            val buffer = ensureCurrentBuffer()
            if (buffer.copy(charBuffer) <= 0) {
                return
            }
        }
    }

    fun asString(): String {
        var count = (buffers.size - 1) * allocateSize + currentBuffer.index
        val charArray = CharArray(count)
        var pos = 0
        for (buffer in buffers) {
            System.arraycopy(buffer.charArray, 0, charArray, pos, buffer.index)
            pos += buffer.index
        }
        return String(charArray)
    }

    private fun ensureCurrentBuffer(): PooledCharArrayBuffer {
        if (currentBuffer.remaining() <= 0) {
            currentBuffer = PooledCharArrayBuffer(allocateSize)
        }
        return currentBuffer
    }

    private class PooledCharArrayBuffer {
        var index = 0
        var capacity: Int
        var charArray: CharArray

        constructor(capacity: Int) {
            this.capacity = capacity
            this.charArray = CharArray(capacity)
        }

        fun copy(charBuffer: CharBuffer): Int {
            val currentRemaining = remaining()
            val charBufferRemaining = charBuffer.remaining()
            return if (charBufferRemaining <= currentRemaining) {
                System.arraycopy(charBuffer.array(), charBuffer.position(), charArray, index, charBufferRemaining)
                index += charBufferRemaining
                0
            } else {
                System.arraycopy(charBuffer.array(), charBuffer.position(), charArray, index, currentRemaining)
                charBuffer.position(charBuffer.position() + currentRemaining)
                index += currentRemaining
                charBuffer.remaining()
            }
        }

        fun remaining(): Int {
            return capacity - index
        }
    }
}