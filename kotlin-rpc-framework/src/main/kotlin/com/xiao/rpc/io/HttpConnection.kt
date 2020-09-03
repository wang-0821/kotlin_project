package com.xiao.rpc.io

import com.xiao.rpc.AbstractCloseableResource
import com.xiao.rpc.ResponseListener
import com.xiao.rpc.Route
import com.xiao.rpc.RunningState
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket

/**
 *
 * @author lix wang
 */
class HttpConnection(
    private val route: Route,
    private val socket: Socket,
    responseListener: ResponseListener?
) : AbstractConnection() {
    private val runningState = RunningState()
    private val closeableResource = object : AbstractCloseableResource(runningState) {}
    private val realResponseListener = responseListener ?: object : ResponseListener {
        override fun afterResponse() {
            synchronized(runningState) {
                runningState.updateState(RunningState.RUNNING, RunningState.READY)
            }
        }
    }

    private var realSocket: Socket? = null
    private var inputStream: InputStream? = null
    private var outputStream: OutputStream? = null
    private var currentResponse: Response? = null

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
        currentResponse = parseToResponse(inputStream!!, realResponseListener)
        return currentResponse!!
    }

    override fun tryClose(keepAliveMills: Int): Boolean {
        val result = closeableResource.tryClose(keepAliveMills)
        if (result) {
            outputStream?.close()
            inputStream?.close()
        }
        return result
    }

    override fun tryUse(): Boolean {
        return closeableResource.tryUse()
    }

    override fun unUse(): Boolean {
        return closeableResource.unUse()
    }
}