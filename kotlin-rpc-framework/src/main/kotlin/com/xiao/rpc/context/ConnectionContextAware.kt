package com.xiao.rpc.context

import com.xiao.base.context.Context
import com.xiao.rpc.Route
import com.xiao.rpc.io.Connection

/**
 *
 * @author lix wang
 */
interface ConnectionContextAware : ClientContextAware<ConnectionContext> {
    override val key: Context.Key<ConnectionContext>
        get() = ConnectionContext.Key

    fun poll(clientContextKey: Context.Key<*>, route: Route): Connection? {
        return getContext(clientContextKey)?.poll(route)
    }

    fun add(clientContextKey: Context.Key<*>, connection: Connection) {
        getContext(clientContextKey)?.add(connection)
    }
}