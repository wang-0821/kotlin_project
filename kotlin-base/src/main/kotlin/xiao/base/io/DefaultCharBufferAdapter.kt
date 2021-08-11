package xiao.base.io

import java.nio.CharBuffer

/**
 *
 * @author lix wang
 */
class DefaultCharBufferAdapter(
    private val bufferSize: Int = xiao.base.CommonConstants.BUFFER_SIZE
) : CharBufferAdapter {
    private val buffers = mutableListOf<UnsafeDirectCharArrayBuffer>()
    private var currentBuf: UnsafeDirectCharArrayBuffer

    init {
        this.currentBuf = UnsafeDirectCharArrayBuffer(this.bufferSize)
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

    private fun ensureCurrentBuffer(): UnsafeDirectCharArrayBuffer {
        if (currentBuf.remaining() <= 0) {
            currentBuf = UnsafeDirectCharArrayBuffer(this.bufferSize)
            buffers.add(currentBuf)
        }
        return currentBuf
    }
}