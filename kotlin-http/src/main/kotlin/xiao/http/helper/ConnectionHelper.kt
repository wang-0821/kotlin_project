package xiao.http.helper

import xiao.base.logging.Logging
import xiao.beans.context.Context
import xiao.http.Client
import xiao.http.Route
import xiao.http.context.ConnectionContextAware
import xiao.http.context.RouteContextAware
import xiao.http.factory.ConnectionFactory
import xiao.http.factory.ConnectionFactorySelector
import xiao.http.factory.SocketFactory
import xiao.http.factory.SocketFactorySelector
import xiao.http.io.Connection

/**
 *
 * @author lix wang
 */
object ConnectionHelper : ConnectionContextAware, RouteContextAware, Logging() {
    fun findConnection(
        client: Client,
        routes: List<Route>,
        connectTimeout: Int
    ): Connection? {
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

    fun getConnectionWithCache(clientContextPoolKey: Context.Key<*>, routes: List<Route>): Connection? {
        for (route in routes) {
            val connection = getConnection(clientContextPoolKey, route)
            if (connection != null) {
                return connection
            }
        }
        return null
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
            val connection = connectionFactory.create(socket, route)
            if (connection.tryUse()) {
                connection
            } else {
                null
            }
        } catch (e: Exception) {
            log.error("Create connection failed, route: $route, ${e.message}.", e)
            null
        }
    }
}