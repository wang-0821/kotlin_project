package com.xiao.base.io

import com.xiao.base.util.IoUtils
import java.nio.CharBuffer

/**
 *
 * @author lix wang
 */
class DefaultCharBufferAdapter(
    private val bufferSize: Int = IoUtils.BUFFER_SIZE
) : CharBufferAdapter {
    private val buffers = mutableListOf<UnsafeCharArrayBuffer>()
    private var currentBuffer: UnsafeCharArrayBuffer

    init {
        this.currentBuffer = UnsafeCharArrayBuffer(this.bufferSize)
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
        return (buffers.size - 1) * bufferSize + currentBuffer.size()
    }

    override fun asString(): String {
        val size = size()
        if (size <= 0) {
            return ""
        }

        val charArray = CharArray(size)
        var pos = 0
        for (buffer in buffers) {
            if (buffer.size() > 0) {
                buffer.writeTo(charArray, pos, 0, buffer.size())
                pos += buffer.size()
            }
        }
        return String(charArray)
    }

    override fun close() {
        buffers
            .forEach {
                it.close()
            }
    }

    private fun ensureCurrentBuffer(): UnsafeCharArrayBuffer {
        if (currentBuffer.remaining() <= 0) {
            currentBuffer = UnsafeCharArrayBuffer(this.bufferSize)
            buffers.add(currentBuffer)
        }
        return currentBuffer
    }
}