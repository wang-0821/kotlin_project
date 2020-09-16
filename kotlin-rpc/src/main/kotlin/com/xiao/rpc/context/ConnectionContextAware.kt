package com.xiao.rpc.context

import com.xiao.base.context.Context
import com.xiao.rpc.Route
import com.xiao.rpc.io.Connection

/**
 *
 * @author lix wang
 */
interface ConnectionContextAware : ClientContextPoolAware {
    fun getConnection(contextPoolKey: Context.Key<*>, route: Route): Connection? {
        return context(contextPoolKey)?.get(route)
    }

    fun addConnection(contextPoolKey: Context.Key<*>, connection: Connection) {
        context(contextPoolKey)?.add(connection)
    }

    fun context(contextPoolKey: Context.Key<*>): ConnectionContext? {
        return getContext(contextPoolKey, KEY) as ConnectionContext?
    }

    companion object {
        val KEY = ConnectionContext.Key
    }
}