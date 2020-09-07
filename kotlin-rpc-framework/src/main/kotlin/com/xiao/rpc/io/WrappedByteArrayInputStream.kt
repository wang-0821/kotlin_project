package com.xiao.rpc.io

import java.io.ByteArrayInputStream

/**
 *
 * @author lix wang
 */
internal class WrappedByteArrayInputStream : ByteArrayInputStream {
    constructor(byteArray: ByteArray, offset: Int, length: Int) : super(byteArray, offset, length)

    fun replace(byteArray: ByteArray, offset: Int, length: Int) {
        buf = byteArray
        pos = offset
        count = (offset + length).coerceAtMost(buf.size)
        mark = offset
    }

    override fun toString(): String {
        return "(pos: ${pos}, count: ${count}, mark: ${mark})"
    }
}