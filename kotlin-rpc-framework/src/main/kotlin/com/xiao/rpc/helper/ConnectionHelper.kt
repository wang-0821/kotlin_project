package com.xiao.rpc.helper

import com.xiao.base.context.Context
import com.xiao.base.logging.Logging
import com.xiao.rpc.Client
import com.xiao.rpc.Route
import com.xiao.rpc.context.ConnectionContextAware
import com.xiao.rpc.context.RouteContextAware
import com.xiao.rpc.factory.ConnectionFactory
import com.xiao.rpc.factory.ConnectionFactorySelector
import com.xiao.rpc.factory.SocketFactory
import com.xiao.rpc.factory.SocketFactorySelector
import com.xiao.rpc.io.Connection

/**
 *
 * @author lix wang
 */
object ConnectionHelper : ConnectionContextAware, RouteContextAware, Logging() {
    fun findConnection(client: Client, routes: List<Route>, connectTimeout: Int): Connection? {
        var connection: Connection?
        return if (client.clientContextPool != null) {
            connection = getConnectionWithCache(client.clientContextPool!!.key, routes)
            if (connection != null) {
                return connection
            }
            connection = createConnection(routes, connectTimeout) {
                removeRoute(client.clientContextPool!!.key, it)
            }
            if (connection != null) {
                addConnection(client.clientContextPool!!.key, connection)
            }
            connection
        } else {
            return createConnection(routes, connectTimeout)
        }
    }

    private fun createConnection(
        routes: List<Route>,
        connectTimeout: Int,
        failBlock: ((Route) -> Unit)? = null
    ): Connection? {
        val socketFactory = SocketFactorySelector.select()
        val connectionFactory = ConnectionFactorySelector.select()
        for (route in routes) {
            val connection = createConnection(socketFactory, connectionFactory, route, connectTimeout)
            if (connection == null) {
                failBlock?.invoke(route)
            } else {
                return connection
            }
        }
        return null
    }

    private fun createConnection(
        socketFactory: SocketFactory,
        connectionFactory: ConnectionFactory,
        route: Route,
        connectTimeout: Int
    ): Connection? {
        return try {
            val socket = socketFactory.createSocket(route, connectTimeout)
            connectionFactory.create(socket, route)
        } catch (e: Exception) {
            log.error("Create connection failed, route: $route, ${e.message}.", e)
            null
        }
    }

    private fun getConnectionWithCache(key: Context.Key<*>, routes: List<Route>): Connection? {
        for (route in routes) {
            val connection = getConnection(key, route)
            if (connection != null) {
                return connection
            }
        }
        return null
    }
}