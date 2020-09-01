package com.xiao.rpc.io

import com.xiao.rpc.AbstractCloseableResource
import com.xiao.rpc.ContentHeaders
import com.xiao.rpc.Route
import com.xiao.rpc.RunningState
import com.xiao.rpc.factory.SslSocketFactorySelector
import com.xiao.rpc.helper.IoHelper.CRLF
import com.xiao.rpc.helper.ResponseHelper
import com.xiao.rpc.tool.JacksonUtils
import java.io.InputStream
import java.net.Socket
import java.net.URLEncoder
import javax.net.ssl.SSLSocket

/**
 *
 * @author lix wang
 */
abstract class AbstractConnection : Connection {
    private val closeableResource = object : AbstractCloseableResource(RunningState()) {}

    override fun writeHeaders(request: Request) {
        val path = request.path()?.let {
            URLEncoder.encode(it, "UTF-8")
        } ?: "/"
        var headerLine = "${request.method().name} $path ${request.protocol().text}$CRLF"
        if (request.header("Host") == null) {
            request.header(Header("Host", request.host() + ":" + request.port()))
        }
        if (request.header("Connection") == null) {
            request.header(Header("Connection", "Keep-Alive"))
        }
        if (request.header(ContentHeaders.ACCEPT_ENCODING.text) == null) {
            request.header(Header(ContentHeaders.ACCEPT_ENCODING.text, "gzip"))
        }
        if (request.header("User-Agent") == null) {
            request.header(Header("User-Agent", "lix-http/${System.getProperty("projectMavenVersion")}"))
        }

        for (header in request.headers()) {
            headerLine += "${header.name}: ${header.value}$CRLF"
        }
        headerLine += CRLF

        write(headerLine.toByteArray())
    }

    override fun writeBody(request: Request) {
        val body = if (request.params().isNotEmpty()) {
            JacksonUtils.serialize(request.params()) + CRLF
        } else {
            CRLF
        }
        write(body.toByteArray())
    }

    override fun tryClose(keepAliveMills: Int): Boolean {
        return closeableResource.tryClose(keepAliveMills)
    }

    override fun tryUse(): Boolean {
        return closeableResource.tryUse()
    }

    override fun unUse(): Boolean {
        return closeableResource.unUse()
    }

    protected fun parseToResponse(inputStream: InputStream): Response {
        return ResponseHelper.parseResponse(inputStream)
    }

    protected fun connectTls(socket: Socket, route: Route): SSLSocket {
        val sslSocket = SslSocketFactorySelector.select().createSSLSocket(socket, route)
        sslSocket.startHandshake()
        return sslSocket
    }
}