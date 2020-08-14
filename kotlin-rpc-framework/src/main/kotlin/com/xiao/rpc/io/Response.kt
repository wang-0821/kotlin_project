package com.xiao.rpc.io

import com.xiao.rpc.ContentHeaders
import com.xiao.rpc.Protocol
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
    var protocol: Protocol
    /**
     * Http status code
     */
    var status: Int
    /**
     * Http headers
     */
    var headers: List<Header>

    /**
     * Http entity
     */
    var content: InputStream

    private val headerMap: Map<String, List<Header>>

    constructor(protocol: Protocol, status: Int, headers: List<Header>, content: InputStream) {
        this.protocol = protocol
        this.status = status
        this.headers = headers
        this.content = content
        this.headerMap = headers.groupBy { it.name.toUpperCase() }
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
        return if (contentLength > 0) {
            IoHelper.inputStreamToString(content, charset!!, contentLength)
        } else {
            IoHelper.inputStreamToString(content, charset!!)
        }
    }

    override fun close() {
        content.close()
    }
}