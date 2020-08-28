package com.xiao.rpc.handler

import com.xiao.rpc.Route
import com.xiao.rpc.StateSocket
import com.xiao.rpc.context.ConnectionContextAware
import com.xiao.rpc.helper.ConnectionHelper
import com.xiao.rpc.helper.RouteHelper
import com.xiao.rpc.helper.SocketHelper
import com.xiao.rpc.io.Response

/**
 *
 * @author lix wang
 */
class ConnectionHandler(override val chain: Chain) : Handler {
    override fun handle(): Response {
        val startTime = System.currentTimeMillis()
        val routes = RouteHelper.findRoutes(chain.client)
        check(routes.isNotEmpty()) {
            "ConnectionHandler can not find valid routes."
        }
        val connection = ConnectionHelper.findConnection(chain.client, routes)
        check(connection != null) {
            "ConnectionHandler can not find valid connection."
        }
        chain.exchange.connection = connection
        val endTime = System.currentTimeMillis()
        println("*** ConnectionHandler cost: ${endTime - startTime} ms")
        return chain.execute()
    }

    private fun createConnection() {
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
            createConnection(routes) {
                SocketHelper.findSocketByCache(it)
            }
        }

        if (chain.exchange.connection == null) {
            createConnection(routes) {
                SocketHelper.findSocket(it, chain.exchange.connectTimeout)
            }
        }

        if (chain.exchange.connection == null) {
            throw NoSuchElementException("Exchange connection must be not null.")
        }
    }

    private fun createConnection(routes: Set<Route>, socketGenerator: (Route) -> StateSocket?) {
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
                connection.validateAndUse()
                add(connection)
                break
            }
        }
    }
}