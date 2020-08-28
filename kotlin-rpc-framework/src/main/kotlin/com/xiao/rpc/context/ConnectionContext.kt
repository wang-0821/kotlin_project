package com.xiao.rpc.context

import com.xiao.base.context.Context
import com.xiao.rpc.Route
import com.xiao.rpc.io.Connection
import java.util.concurrent.ConcurrentHashMap

/**
 *
 * @author lix wang
 */
class ConnectionContext : ClientContextAware<ConnectionContext> {
    companion object Key : Context.Key<ConnectionContext>
    override val key: Context.Key<ConnectionContext>
        get() = Key

    private val connectionPool = ConcurrentHashMap<Route, MutableList<Connection>>()

    fun poll(route: Route): Connection? {
        listOf<Any>(1, 2)
        var connection: Connection? = null
        connectionPool[route]?.let {
            for (i in it.size - 1..0) {
                connection = it[i]
                val state = connection!!.validateAndUse()
                if (state > 0) {
                    break
                } else {
                    connection = null
                }
            }
        }
        return connection
    }

    fun add(connection: Connection) {
        synchronized(connectionPool) {
            var connections = connectionPool[connection.route()]
            if (connections == null) {
                connections = mutableListOf(connection)
                connectionPool[connection.route()] = connections
            } else {
                connections.add(connection)
            }
        }
    }
}