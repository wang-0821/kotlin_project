package com.xiao.rpc.context

import com.xiao.base.annotation.ContextInject
import com.xiao.base.context.AbstractContext
import com.xiao.base.context.Context
import com.xiao.rpc.Address
import com.xiao.rpc.Route
import com.xiao.rpc.io.Connection
import java.util.concurrent.ConcurrentHashMap

/**
 *
 * @author lix wang
 */
@ContextInject
class ConnectionContext : AbstractContext(ConnectionContext) {
    companion object Key : Context.Key<ConnectionContext>

    private val connectionPool = ConcurrentHashMap<Route, MutableSet<Connection>>()

    @Synchronized fun poll(route: Route): Set<Connection>? {
        var connection: Connection? = null
        connectionPool[route]?.let {
            val iterator = it.iterator()
            while (iterator.hasNext()) {
                connection = iterator.next()
                if (connection!!.validate()) {
                    iterator.remove()
                    break
                } else {
                    connection = null
                }
            }
        }
        return connection
    }
}