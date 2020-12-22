package com.xiao.rpc.stream

import java.io.InputStream

/**
 *
 * @author lix wang
 */
class IdentityInputStream(private val inputStream: InputStream) : InputStream() {
    private var closed = false

    override fun available(): Int {
        if (closed) {
            return 0
        }
        return inputStream.available()
    }

    override fun read(): Int {
        if (closed) {
            return -1
        }
        return inputStream.read()
    }

    override fun read(b: ByteArray): Int {
        if (closed) {
            return -1
        }
        return inputStream.read(b)
    }

    override fun read(b: ByteArray, off: Int, len: Int): Int {
        if (closed) {
            return -1
        }
        return inputStream.read(b, off, len)
    }

    override fun close() {
        closed = true
    }
}