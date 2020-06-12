package com.xiao.rpc.handler

import com.xiao.base.exception.HttpStatus
import com.xiao.base.exception.KtException
import com.xiao.rpc.Response
import com.xiao.rpc.StateSocket
import com.xiao.rpc.acquireRoutes
import com.xiao.rpc.acquireSocket
import com.xiao.rpc.context.SocketContextAware
import com.xiao.rpc.exception.SocketException
import com.xiao.rpc.factory.ConnectionFactorySelector
import com.xiao.rpc.factory.SocketFactorySelector
import java.io.IOException

/**
 *
 * @author lix wang
 */
class ConnectionHandler(override val chain: Chain) : Handler, SocketContextAware {
    override fun handle(): Response {
        connect(chain)
        return chain.execute()
    }

    @Throws(KtException::class)
    private fun connect(chain: Chain) {
        chain.exchange?.connection?.let {
            return
        }

        if (chain.exchange == null) {
            chain.exchange = Exchange()
        }
        val connectionFactory = ConnectionFactorySelector.select()
        // check socket cache
        do {
            val socket = poll(chain.request.address)
            socket?.let {
                chain.exchange!!.connection = connectionFactory.create(it)
            }
        } while (socket != null && chain.exchange!!.connection == null)

        // socket context have no valid socket
        chain.exchange!!.connection ?: kotlin.run {
            var routes = chain.exchange?.routes
            if (routes.isNullOrEmpty()) {
                routes = chain.request.address.acquireRoutes()
            }

            while (routes.iterator().hasNext() && chain.exchange!!.connection == null) {
                val route = routes.iterator().next()
                var socket: StateSocket? = null
                try {
                    socket = route.acquireSocket(chain.client.connectTimeout)
                    chain.exchange!!.connection = connectionFactory.create(socket)
                    add(socket)
                } catch (e: IOException) {
                    chain.exchange!!.connection = null
                    socket?.let {
                        remove(it)
                    }
                    socket?.close()
                }
            }
        }

        if (chain.exchange!!.connection == null) {
            throw KtException()
                .statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                .message("Connect failed: ${chain.request}")
        }
    }
}