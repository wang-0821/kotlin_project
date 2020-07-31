package com.xiao.rpc.handler

import com.xiao.rpc.Route
import com.xiao.rpc.StateSocket
import com.xiao.rpc.context.ConnectionContextAware
import com.xiao.rpc.exception.ConnectionException
import com.xiao.rpc.helper.SocketHelper
import com.xiao.rpc.io.Response

/**
 *
 * @author lix wang
 */
class ConnectionHandler(override val chain: Chain) : Handler, ConnectionContextAware {
    override fun handle(): Response {
        findConnection()
        return chain.execute()
    }

    private fun findConnection() {
        if (chain.exchange.connection != null) {
            return
        }
        val routes = chain.exchange.acquireRoutes()

        // find cached connection
        for (route in routes) {
            poll(route)?.let {
                chain.exchange.connection = it
            }
        }

        if (chain.exchange.connection == null) {
            findConnection(routes) {
                SocketHelper.findSocketByCache(it)
            }
        }

        if (chain.exchange.connection == null) {
            findConnection(routes) {
                SocketHelper.findSocket(it, chain.client.connectTimeout)
            }
        }

        if (chain.exchange.connection == null) {
            throw ConnectionException.noAvailableConnection("${this.javaClass.simpleName} acquire connection failed.")
        }
    }

    private fun findConnection(routes: Set<Route>, socketGenerator: (Route) -> StateSocket?) {
        for (route in routes) {
            // find cached socket
            val socket = socketGenerator(route)
            val connection = try {
                socket?.acquireConnection()
            } catch (e: Exception) {
                null
            }
            if (connection != null) {
                chain.exchange.connection = connection
                add(connection)
                break
            }
        }
    }
}