package com.xiao.rpc.context

import com.xiao.base.context.ContextAware
import com.xiao.rpc.Address
import com.xiao.rpc.Route
import com.xiao.rpc.StateSocket
import com.xiao.rpc.io.Connection

/**
 *
 * @author lix wang
 */
interface RouteContextAware : ContextAware {
    fun get(address: Address): Set<Route>? {
        return get(RouteContext.Key)?.get(address)
    }

    fun add(address: Address, routes: Set<Route>): Boolean {
        return get(RouteContext.Key)?.let {
            it.add(address, routes)
        } ?: false
    }
}

interface SocketContextAware : ContextAware {
    fun poll(address: Address): StateSocket? {
        return get(SocketContext.Key)?.poll(address)
    }

    fun remove(socket: StateSocket): Boolean {
        return get(SocketContext.Key)?.remove(socket) ?: false
    }

    fun add(socket: StateSocket): Boolean {
        return get(SocketContext.Key)?.add(socket) ?: false
    }
}

interface ConnectionContextAware : ContextAware {
    fun poll(address: Address): Connection? {
        return get(ConnectionContext.Key)?.poll(address)
    }
}