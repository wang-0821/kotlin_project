package com.xiao.base.io

import java.nio.CharBuffer

/**
 *
 * @author lix wang
 */
class DefaultBufferAdapter : BufferAdapter {
    private val buffers = mutableListOf<PooledCharArrayBuffer>()
    private var currentBuffer: PooledCharArrayBuffer
    private val bufferSize: Int

    constructor(bufferSize: Int = IoHelper.BUFFER_SIZE) {
        this.bufferSize = bufferSize
        this.currentBuffer = PooledCharArrayBuffer(this.bufferSize)
        buffers.add(currentBuffer)
    }

    override fun appendCharBuffer(charBuffer: CharBuffer) {
        while (true) {
            val buffer = ensureCurrentBuffer()
            if (buffer.copy(charBuffer) <= 0) {
                return
            }
        }
    }

    override fun size(): Int {
        return (buffers.size - 1) * bufferSize + currentBuffer.index
    }

    override fun asString(): String {
        val size = size()
        if (size <= 0) {
            return ""
        }

        val charArray = CharArray(size)
        var pos = 0
        for (buffer in buffers) {
            if (buffer.index > 0) {
                System.arraycopy(buffer.charArray!!, 0, charArray, pos, buffer.index)
                pos += buffer.index
            }
        }
        return String(charArray)
    }

    private fun ensureCurrentBuffer(): PooledCharArrayBuffer {
        if (currentBuffer.remaining() <= 0) {
            currentBuffer = PooledCharArrayBuffer(this.bufferSize)
            buffers.add(currentBuffer)
        }
        return currentBuffer
    }

    private class PooledCharArrayBuffer {
        var index = 0
        val capacity: Int
        var charArray: CharArray? = null

        constructor(capacity: Int) {
            this.capacity = capacity
        }

        fun copy(charBuffer: CharBuffer): Int {
            val charBufferRemaining = charBuffer.remaining()
            if (charBufferRemaining <= 0) {
                return 0
            }
            charArray = charArray ?: CharArray(capacity)
            val currentRemaining = remaining()
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