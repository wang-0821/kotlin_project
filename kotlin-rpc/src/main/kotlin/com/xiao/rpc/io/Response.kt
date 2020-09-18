package com.xiao.rpc.io

import com.xiao.rpc.Protocol
import com.xiao.rpc.ResponseListener
import com.xiao.rpc.stream.ChunkedInputStream
import java.io.Closeable
import java.net.Socket
import java.time.Instant

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
    private val content: HttpResponseContent?

    /**
     * Header map
     */
    private val headerMap: Map<String, List<Header>>

    /**
     * Response listener
     */
    private val listener: ResponseListener?

    private val socket: Socket?

    constructor(
        protocol: Protocol,
        status: Int,
        headers: List<Header>,
        content: HttpResponseContent?,
        listener: ResponseListener?,
        socket: Socket?
    ) {
        this.protocol = protocol
        this.status = status
        this.headers = headers
        this.content = content
        this.headerMap = headers.groupBy { it.name.toUpperCase() }
        this.listener = listener
        this.socket = socket
    }

    fun asString(): String? {
        try {
            val result = content?.asString()
            println("Total read count size: ${ChunkedInputStream.totalLocal.get()}")
            return result
        } catch (e: Exception) {
            println("Socket $socket, time ${Instant.now()}")
          throw e
        } finally {
            listener?.afterResponse()
        }
    }

    override fun close() {
        content?.close()
    }
}