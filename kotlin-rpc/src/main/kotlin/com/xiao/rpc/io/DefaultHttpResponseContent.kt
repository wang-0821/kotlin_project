package com.xiao.rpc.io

import com.xiao.base.logging.Logging
import com.xiao.rpc.helper.IoHelper
import java.io.InputStream
import java.nio.charset.Charset

/**
 *
 * @author lix wang
 */
class DefaultHttpResponseContent(
    private val contentType: String?,
    private val contentLength: Long,
    private val charset: Charset?,
    private val inputStream: InputStream
) : HttpResponseContent {
    override fun contentType(): String? {
        return contentType
    }

    override fun contentLength(): Long {
        return contentLength
    }

    override fun content(): InputStream? {
        return inputStream
    }

    override fun asString(): String? {
        val startTime = System.currentTimeMillis()
        val charset = charset ?: Charsets.UTF_8
        val result = if (contentLength > 0) {
            IoHelper.contentAsString(inputStream, charset, contentLength)
        } else {
            IoHelper.contentAsString(inputStream, charset)
        }
        log.info("Content as string consume: ${System.currentTimeMillis() - startTime}")
        return result
    }

    override fun close() {
        inputStream.close()
    }

    companion object : Logging()
}