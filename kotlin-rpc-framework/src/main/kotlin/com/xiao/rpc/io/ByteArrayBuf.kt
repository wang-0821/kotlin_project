package com.xiao.rpc.io

import com.xiao.rpc.helper.IoHelper
import java.io.Closeable
import java.nio.ByteBuffer
import java.nio.CharBuffer
import java.nio.charset.Charset

/**
 *
 * @author lix wang
 */
class ByteArrayBuf : Closeable {
    private var bytes: ByteArray
    private var index: Int = -1
    private var capacity: Int = 0
    private var closed = false

    constructor(capacity: Int = IoHelper.BUFFER_SIZE) {
        this.capacity = capacity
        this.bytes = ByteArray(capacity)
    }

    @Throws(IllegalStateException::class)
    fun add(byte: Byte) {
        if (ensureCapacity()) {
            bytes[++index] = byte
        } else {
            throw IllegalStateException("${this::class.java.simpleName} is closed.")
        }
    }

    override fun toString(): String {
        return if (bytes.isNotEmpty()) {
            bytesToString(null)
        } else ""
    }

    fun clear() {
        index = -1
    }

    private fun bytesToString(charset: Charset?): String {
        val charsetDecoder = (charset ?: Charset.defaultCharset()).newDecoder()
        val byteBuffer = ByteBuffer.wrap(bytes)
        byteBuffer.position(index + 1)
        byteBuffer.flip()
        val charArray = CharArray(index + 1)
        val charBuffer = CharBuffer.wrap(charArray)
        charsetDecoder.decode(byteBuffer, charBuffer, true)
        byteBuffer.clear()
        charBuffer.flip()
        return String(charBuffer.array(), 0, charBuffer.limit())
    }

    private fun ensureCapacity(): Boolean {
        if (closed) {
            return false
        }
        if (limit() <= 0) {
            expand()
        }
        return true
    }

    private fun limit(): Int {
        return capacity - index - 1
    }

    private fun expand() {
        capacity = (capacity * 1.5).toInt()
        val newBytes = ByteArray(capacity)
        System.arraycopy(bytes, 0, newBytes, 0, index + 1)
        bytes = newBytes
    }

    override fun close() {
        index = -1
        capacity = 0
        this.closed = true
    }
}