package xiao.http.io

import xiao.http.Protocol
import xiao.http.ResponseListener
import java.io.Closeable
import java.net.Socket

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
            return content?.asString()
        } finally {
            listener?.afterResponse()
        }
    }

    override fun close() {
        content?.close()
    }
}