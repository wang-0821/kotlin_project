package com.xiao.rpc.helper

import com.xiao.rpc.Route
import com.xiao.rpc.StateSocket
import com.xiao.rpc.context.RouteContextAware
import com.xiao.rpc.context.SocketContextAware

/**
 *
 * @author lix wang
 */
object SocketHelper : SocketContextAware, RouteContextAware {
    fun findSocket(route: Route, connectTimeout: Int): StateSocket? {
        var socket: StateSocket? = try {
            route.acquireSocket(connectTimeout)
        } catch (e: Exception) {
            null
        }
        if (socket != null) {
            add(socket)
            add(route.address, route)
        }
        return socket
    }

    fun findSocketByCache(route: Route): StateSocket? {
        return poll(route)
    }
}