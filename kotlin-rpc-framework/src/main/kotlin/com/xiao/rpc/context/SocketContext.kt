package com.xiao.rpc.context

import com.xiao.base.annotation.ContextInject
import com.xiao.base.context.AbstractContext
import com.xiao.base.context.Context
import com.xiao.rpc.Route
import com.xiao.rpc.StateSocket
import java.util.concurrent.ConcurrentHashMap

/**
 *
 * @author lix wang
 */
@ContextInject
class SocketContext : AbstractContext(SocketContext) {
    companion object Key : Context.Key<SocketContext>
    private val socketPool = ConcurrentHashMap<Route, MutableSet<StateSocket>>()

    fun poll(route: Route): StateSocket? {
        var socket: StateSocket? = null
        socketPool[route]?.let {
            val iterator = it.iterator()
            while (iterator.hasNext()) {
                socket = iterator.next()
                if (socket!!.validateAndUse()) {
                    break
                } else {
                    socket = null
                }
            }
        }
        return socket
    }

    fun remove(socket: StateSocket): Boolean {
        return socketPool[socket.route]?.remove(socket) ?: false
    }

    fun add(socket: StateSocket): Boolean {
        synchronized(socketPool) {
            return if (socketPool[socket.route] == null) {
                socketPool[socket.route] = mutableSetOf(socket)
                true
            } else {
                socketPool[socket.route]!!.add(socket)
            }
        }
    }
}