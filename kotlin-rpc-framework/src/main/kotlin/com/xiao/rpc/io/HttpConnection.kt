package com.xiao.rpc.io

import com.xiao.rpc.Route
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket

/**
 *
 * @author lix wang
 */
class HttpConnection(
    private val route: Route,
    private val socket: Socket
) : AbstractConnection() {
    private var realSocket: Socket? = null
    private var inputStream: InputStream? = null
    private var outputStream: OutputStream? = null

    override fun connect() {
        if (route.address.isTls) {
            this.realSocket = connectTls(socket, route)
        } else {
            this.realSocket = socket
        }
        this.inputStream = realSocket?.getInputStream()
        this.outputStream = realSocket?.getOutputStream()
    }

    override fun route(): Route {
        return route
    }

    override fun write(message: ByteArray) {
        outputStream?.write(message)
    }


    override fun finishRequest() {
        outputStream?.flush()
    }

    override fun response(exchange: Exchange): Response {
        return parseToResponse(inputStream!!)
    }

    override fun tryClose(keepAliveMills: Int): Boolean {
        val result = super.tryClose(keepAliveMills)
        if (result) {
            outputStream?.close()
            inputStream?.close()
        }
        return result
    }
}