package com.xiao.base.io

import java.nio.CharBuffer

/**
 *
 * @author lix wang
 */
interface CharBufferAdapter : AutoCloseable {
    fun appendCharBuffer(charBuffer: CharBuffer)

    fun size(): Int

    fun asString(): String
}