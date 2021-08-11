package xiao.http.context

import xiao.beans.context.Context
import xiao.http.Route
import xiao.http.io.Connection

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
        @JvmField
        val KEY = ConnectionContext.Key
    }
}