package com.xiao.rpc.context

import com.xiao.base.context.Context
import com.xiao.base.logging.Logging
import com.xiao.rpc.Cleaner
import com.xiao.rpc.Route
import com.xiao.rpc.io.Connection
import java.util.concurrent.ConcurrentHashMap

/**
 *
 * @author lix wang
 */
class ConnectionContext(private val contextConfig: ClientContextConfig) : Cleaner, Context {
    companion object Key : Context.Key<ConnectionContext>, Logging()
    override val key: Context.Key<ConnectionContext>
        get() = Key

    private val connectionPool = ConcurrentHashMap<Route, MutableList<Connection>>()

    fun get(route: Route): Connection? {
        var connection: Connection? = null
        connectionPool[route]?.let {
            for (i in it.size - 1..0) {
                connection = it[i]
                if (connection!!.tryUse()) {
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
            if (isFull(connection.route())) {
                log.warn("Connection-${connection.route().address} is over count ${contextConfig.singleCorePoolSize}.")
                return
            }
            var connections = connectionPool[connection.route()]
            if (connections == null) {
                connections = mutableListOf(connection)
                connectionPool[connection.route()] = connections
            } else {
                connections.add(connection)
            }
        }
    }

    private fun isFull(route: Route): Boolean {
        val pooledSize = connectionPool[route]?.size ?: 0
        return contextConfig.singleCorePoolSize <= pooledSize
    }

    override fun cleanup() {
        var cleaned = 0
        log.info("Start cleanup ConnectionContext.")
        val emptyRoutes = mutableListOf<Route>()
        connectionPool.entries.forEach { entry ->
            entry.value.forEach { connection ->
                if (connection.tryClose(contextConfig.timeUnit.toMillis(contextConfig.idleTimeout))) {
                    entry.value.remove(connection)
                    cleaned++
                    if (entry.value.isEmpty()) {
                        emptyRoutes.add(entry.key)
                    }
                }
            }
        }
        if (emptyRoutes.isNotEmpty())  {
            removeEntries(emptyRoutes)
        }
        log.debug("Cleaned $cleaned connections.")
    }

    private fun removeEntries(routes: List<Route>) {
        synchronized(connectionPool) {
            routes.forEach {
                val connections = connectionPool[it]
                if (connections != null && connections.isEmpty()) {
                    connectionPool.remove(it)
                }
            }
        }
    }
}