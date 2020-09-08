package com.xiao.rpc.io

import com.xiao.rpc.ContentHeaders
import com.xiao.rpc.Protocol
import com.xiao.rpc.ResponseListener
import com.xiao.rpc.helper.IoHelper
import java.io.Closeable
import java.io.InputStream
import java.nio.charset.Charset

/**
 *
 * @author lix wang
 */
class Response : Closeable {
    /**
     * Http version
     */
    val protocol: Protocol

    /**
     * Http status code
     */
    val status: Int

    /**
     * Http headers
     */
    val headers: List<Header>

    /**
     * Http entity
     */
    val content: InputStream

    /**
     * Header map
     */
    private val headerMap: Map<String, List<Header>>

    /**
     * Response listener
     */
    private val listener: ResponseListener?

    constructor(
        protocol: Protocol,
        status: Int,
        headers: List<Header>,
        content: InputStream,
        listener: ResponseListener?
    ) {
        this.protocol = protocol
        this.status = status
        this.headers = headers
        this.content = content
        this.headerMap = headers.groupBy { it.name.toUpperCase() }
        this.listener = listener
    }

    fun contentAsString(): String {
        val contentLength = headerMap[ContentHeaders.CONTENT_LENGTH.text.toUpperCase()]?.get(0)?.value?.toInt() ?: -1
        var charset: Charset? = null
        headerMap[ContentHeaders.CONTENT_TYPE.text.toUpperCase()]?.get(0)?.let {
            val splits = it.value.split(";")
            for (split in splits) {
                if (split.trimStart().startsWith("charset")) {
                     charset = Charset.forName(split.split("=")[1].trimStart())
                }
            }
        }
        charset = charset ?: Charsets.UTF_8
        val result = if (contentLength > 0) {
            IoHelper.contentAsString(content, charset!!, contentLength)
        } else {
            IoHelper.contentAsString(content, charset!!)
        }
        close()
        return result
    }

    override fun close() {
        content.close()
        listener?.afterResponse()
    }
}