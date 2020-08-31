package com.xiao.rpc.helper

import com.xiao.base.context.Context
import com.xiao.rpc.Client
import com.xiao.rpc.Route
import com.xiao.rpc.context.ConnectionContextAware
import com.xiao.rpc.factory.ConnectionFactorySelector
import com.xiao.rpc.io.Connection

/**
 *
 * @author lix wang
 */
object ConnectionHelper : ConnectionContextAware {
    fun findConnection(client: Client, routes: List<Route>, connectTimeout: Int): Connection? {
        var connection: Connection?

        // use client context pool
        if (client.clientContextPool != null) {
            // find cached connection
            connection = getConnectionWithCache(client.clientContextPool!!.key, routes)
            if (connection != null) {
                return connection
            }
            // need create connection
            // find socket by cache
            connection = createConnection(client, routes, connectTimeout)
            if (connection != null) {
                add(client.clientContextPool!!.key, connection)
            }
            return connection
        } else {
            return createConnection(client, routes, connectTimeout)
        }
    }

    private fun getConnectionWithCache(key: Context.Key<*>, routes: List<Route>): Connection? {
        for (route in routes) {
            val connection = get(key, route)
            if (connection != null) {
                return connection
            }
        }
        return null
    }

    private fun createConnection(client: Client, routes: List<Route>, connectTimeout: Int): Connection? {
        val socket = SocketHelper.findSocket(client, routes, connectTimeout)
        if (socket != null) {
            return try {
                val connectionFactory = ConnectionFactorySelector.select()
                connectionFactory.create(socket)
            } catch (e: Exception) {
                null
            }
        }
        return null
    }
}