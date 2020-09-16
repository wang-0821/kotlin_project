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
    private var reachEnd = false
    private var closed = false

    override fun available(): Int {
        return if (reachEnd) {
            -1
        } else {
            contentLength - pos
        }
    }

    override fun read(): Int {
        return if (available() > 0) {
            inputStream.read()
        } else {
            -1
        }
    }

    override fun read(b: ByteArray, off: Int, len: Int): Int {
        val read = inputStream.read(b, off, available().coerceAtMost(len))
        if (read < 0) {
            reachEnd = true
            return -1
        }
        pos += read
        return read
    }

    override fun close() {
        closed = true
        inputStream.close()
    }
}