package com.xiao.rpc.io

import java.io.Closeable
import java.io.IOException
import java.io.InputStream

/**
 *
 * @author lix wang
 */
interface HttpEntity : Closeable {
    @Throws(IOException::class)
    fun content(): InputStream

    fun contentAsString(): String

    override fun close() {
        content().close()
    }
}