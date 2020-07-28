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
        return get(RouteContext.Key)?.add(address, routes) ?: false
    }

    fun add(address: Address, route: Route): Boolean {
        return get(RouteContext.Key)?.add(address, route) ?: false
    }

    fun remove(route: Route): Boolean {
        return get(RouteContext.Key)?.remove(route) ?: false
    }
}

interface SocketContextAware : ContextAware {
    fun poll(route: Route): StateSocket? {
        return get(SocketContext.Key)?.poll(route)
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