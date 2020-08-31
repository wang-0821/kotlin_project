package com.xiao.rpc.io

import com.xiao.rpc.Route
import com.xiao.rpc.RunningState
import com.xiao.rpc.StateSocket
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket

/**
 *
 * @author lix wang
 */
class HttpConnection(
    private val route: Route,
    private val socket: StateSocket
) : AbstractConnection() {
    var exchange: Exchange? = null
    private val state = RunningState()

    private var realSocket: Socket? = null
    private var inputStream: InputStream? = null
    private var outputStream: OutputStream? = null
    private var requestStartTime: Long = -1

    override fun connect() {
        if (route.address.isTls) {
            this.realSocket = connectTls(socket, route)
        } else {
            this.realSocket = socket
        }
        this.inputStream = realSocket?.getInputStream()
        this.outputStream = realSocket?.getOutputStream()
    }

    override fun validateAndUse(): Int {
        synchronized(state) {
            if (!checkActivate()) {
                return -1
            }
            return if (state.validateAndUse()) {
                1
            } else {
                0
            }
        }
    }

    override fun route(): Route {
        return route
    }

    override fun write(message: ByteArray) {
        outputStream?.write(message)
    }


    override fun finishRequest() {
        outputStream?.flush()
        requestStartTime = System.currentTimeMillis()
    }

    override fun response(exchange: Exchange): Response {
        return parseToResponse(inputStream!!)
    }

    override fun close() {
        this.inputStream?.close()
        this.outputStream?.close()
    }

    override fun cleanup() {
    }

    private fun checkActivate(): Boolean {
        val currentTime = System.currentTimeMillis()
        return if (requestStartTime > 0 && currentTime - requestStartTime > requestActivateTime) {
            close()
            false
        } else {
            true
        }
    }
}