package com.xiao.rpc.io

import com.xiao.base.io.IoHelper.CRLF
import com.xiao.base.util.JsonUtils
import com.xiao.rpc.ContentHeaders
import com.xiao.rpc.ResponseListener
import com.xiao.rpc.Route
import com.xiao.rpc.factory.SslSocketFactorySelector
import com.xiao.rpc.helper.ResponseHelper
import java.io.InputStream
import java.net.Socket
import java.net.URLEncoder
import javax.net.ssl.SSLSocket

/**
 *
 * @author lix wang
 */
abstract class AbstractConnection : Connection {
    override fun writeHeaders(request: Request) {
        val headerMap = request.headers().associateByTo(mutableMapOf(), { it.name })
        val path = request.path()?.let {
            URLEncoder.encode(it, "UTF-8")
        } ?: "/"
        var headerLine = "${request.method().name} $path ${request.protocol().text}$CRLF"

        if (headerMap["Host"] == null) {
            headerMap["Host"] = Header("Host", request.host() + ":" + request.port())
        }
        if (headerMap["Connection"] == null) {
            headerMap["Connection"] = Header("Connection", "Keep-Alive")
        }
        if (headerMap[ContentHeaders.ACCEPT_ENCODING.text] == null) {
            headerMap[ContentHeaders.ACCEPT_ENCODING.text] = Header(ContentHeaders.ACCEPT_ENCODING.text, "gzip")
        }
        if (headerMap["User-Agent"] == null) {
            headerMap["User-Agent"] = Header("User-Agent", "lix-http-client")
        }

        for (header in headerMap.values) {
            headerLine += "${header.name}: ${header.value}$CRLF"
        }
        headerLine += CRLF

        write(headerLine.toByteArray())
    }

    override fun writeBody(request: Request) {
        val body = if (request.params().isNotEmpty()) {
            JsonUtils.serialize(request.params()) + CRLF
        } else {
            CRLF
        }
        write(body.toByteArray())
    }

    protected fun parseToResponse(inputStream: InputStream, responseListener: ResponseListener, socket: Socket): Response {
        return ResponseHelper.parseResponse(inputStream, responseListener, socket)
    }

    protected fun connectTls(socket: Socket, route: Route): SSLSocket {
        val sslSocket = SslSocketFactorySelector.select().createSSLSocket(socket, route)
        sslSocket.startHandshake()
        return sslSocket
    }
}