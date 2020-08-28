package com.xiao.rpc.context

import com.xiao.base.context.Context
import com.xiao.rpc.Route
import com.xiao.rpc.StateSocket

/**
 *
 * @author lix wang
 */
interface SocketContextAware : ClientContextAware<SocketContext> {
    override val key: Context.Key<SocketContext>
        get() = SocketContext.Key

    fun poll(clientContextKey: Context.Key<*>, route: Route): StateSocket? {
        return getContext(clientContextKey)?.poll(route)
    }

    fun remove(clientContextKey: Context.Key<*>, socket: StateSocket): Boolean {
        return getContext(clientContextKey)?.remove(socket) ?: false
    }

    fun add(clientContextKey: Context.Key<*>, socket: StateSocket): Boolean {
        return getContext(clientContextKey)?.add(socket) ?: false
    }
}