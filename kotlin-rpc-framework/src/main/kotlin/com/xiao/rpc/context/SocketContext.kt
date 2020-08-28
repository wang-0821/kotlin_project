package com.xiao.rpc.context

import com.xiao.base.context.Context
import com.xiao.rpc.Route
import com.xiao.rpc.StateSocket
import java.util.concurrent.ConcurrentHashMap

/**
 *
 * @author lix wang
 */
class SocketContext : ClientContextAware<SocketContext> {
    companion object Key : Context.Key<SocketContext>
    override val key: Context.Key<SocketContext>
        get() = Key

    private val socketPool = ConcurrentHashMap<Route, MutableList<StateSocket>>()

    fun poll(route: Route): StateSocket? {
        var socket: StateSocket? = null
        socketPool[route]?.let {
            for (i in it.size - 1..0) {
                socket = it[i]
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
            var list = socketPool[socket.route]
            if (list == null) {
                list = mutableListOf()
                socketPool[socket.route] = list
            }
            return if (list.contains(socket)) {
                false
            } else {
                list.add(socket)
            }
        }
    }
}