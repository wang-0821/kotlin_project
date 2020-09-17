package com.xiao.rpc.stream

import java.io.InputStream

/**
 *
 * @author lix wang
 */
class ContentLengthInputStream(
    private val inputStream: InputStream,
    private val contentLength: Int
): InputStream() {
    private var pos = 0
    private var eof = false
    private var closed = false

    override fun available(): Int {
        return if (eof || closed) {
            0
        } else {
            contentLength - pos
        }
    }

    override fun read(): Int {
        if (available() <= 0) {
            return -1
        }
        val bytecode = inputStream.read()
        val read = if (bytecode == -1) {
            -1
        } else {
            1
        }
        updatePos(read)
        return bytecode
    }

    override fun read(b: ByteArray, off: Int, len: Int): Int {
        if (available() <= 0) {
            return -1
        }
        val read = inputStream.read(b, off, available().coerceAtMost(len))
        updatePos(read)
        return read
    }

    override fun close() {
        closed = true
    }

    private fun updatePos(read: Int) {
        if (read < 0) {
            eof = true
            return
        } else {
            pos += read
        }
    }
}