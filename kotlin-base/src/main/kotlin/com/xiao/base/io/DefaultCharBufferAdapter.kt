package com.xiao.base.io

import com.xiao.base.CommonConstants
import java.nio.CharBuffer

/**
 *
 * @author lix wang
 */
class DefaultCharBufferAdapter(
    private val bufferSize: Int = CommonConstants.BUFFER_SIZE
) : CharBufferAdapter {
    private val buffers = mutableListOf<UnsafeCharArrayBuffer>()
    private var currentBuf: UnsafeCharArrayBuffer

    init {
        this.currentBuf = UnsafeCharArrayBuffer(this.bufferSize)
        buffers.add(currentBuf)
    }

    override fun appendCharBuffer(charBuffer: CharBuffer) {
        while (true) {
            val buffer = ensureCurrentBuffer()
            if (buffer.append(charBuffer) <= 0) {
                return
            }
        }
    }

    override fun size(): Int {
        return (buffers.size - 1) * bufferSize + currentBuf.size()
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
        if (currentBuf.remaining() <= 0) {
            currentBuf = UnsafeCharArrayBuffer(this.bufferSize)
            buffers.add(currentBuf)
        }
        return currentBuf
    }
}