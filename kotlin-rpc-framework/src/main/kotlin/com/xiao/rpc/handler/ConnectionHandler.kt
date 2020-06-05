package com.xiao.rpc.handler

import com.xiao.base.context.ContextAware
import com.xiao.base.exception.HttpStatus
import com.xiao.base.exception.KtException
import com.xiao.rpc.*
import com.xiao.rpc.exception.ConnectionException
import com.xiao.rpc.io.ConnectionSelector
import com.xiao.rpc.io.Exchange
import java.io.IOException

/**
 *
 * @author lix wang
 */
class ConnectionHandler(override val chain: Chain) : Handler, ContextAware {
    override fun handle(): Response {
        connect(chain)
        return chain.execute()
    }

    @Throws(KtException::class)
    private fun connect(chain: Chain) {
        chain.exchange?.connection?.let {
            return
        }

        var routes = chain.exchange?.routes
        if (routes.isNullOrEmpty()) {
            routes = chain.request.address.acquireRoutes()
        }
        if (chain.exchange == null) {
            chain.exchange = Exchange()
        }
        val connectionFactory = ConnectionSelector.select(chain.request.address.protocol)
        // check socket cache
        val socketContext = get(SocketContext.Key)
        socketContext?.let {
            var socket: StateSocket?
            do {
                socket = it.poll(chain.request.address)
                socket?.let {
                    try {
                        chain.exchange!!.connection = connectionFactory.create(it)
                    } catch (e : Exception) {
                        chain.exchange!!.connection = null
                        socketContext.remove(it)
                    }
                }
            } while (socket != null && chain.exchange!!.connection == null)
        }

        // socket context have no valid socket
        chain.exchange!!.connection ?: kotlin.run {
            for (route in routes) {
                val socket = chain.client.socketFactory.createSocket(route)
                try {
                    if (chain.client.connectTimeout > 0) {
                        socket.connect(socket.route.inetSocketAddress, chain.client.connectTimeout)
                    } else {
                        socket.connect(socket.route.inetSocketAddress)
                    }
                    chain.exchange!!.connection = connectionFactory.create(socket)
                    socketContext?.add(socket)
                    break
                } catch (e: IOException) {
                    socket.close()
                    socketContext?.remove(socket)
                    chain.client.connectTimeoutHandler?.invoke(ConnectionException.connectTimeout("route: $route"))
                }
            }
        }

        if (chain.exchange!!.connection == null) {
            throw ConnectionException(KtException()
                .statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                .message("Connect failed: ${chain.request}")
            )
        }
    }
}