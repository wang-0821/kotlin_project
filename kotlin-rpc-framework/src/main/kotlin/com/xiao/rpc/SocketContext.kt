package com.xiao.rpc

import com.xiao.base.context.AbstractContext
import com.xiao.base.context.Context
import java.util.concurrent.ConcurrentHashMap

/**
 *
 * @author lix wang
 */
class SocketContext : AbstractContext(SocketContext) {
    companion object Key : Context.Key<SocketContext>

    private val socketPool = ConcurrentHashMap<Address, MutableSet<StateSocket>>()

    @Synchronized fun poll(address: Address): StateSocket? {
        var socket: StateSocket? = null
        socketPool[address]?.let {
            val iterator = it.iterator()
            while (iterator.hasNext()) {
                socket = iterator.next()
                if (socket!!.validate()) {
                    break
                } else {
                    socket = null
                }
            }
        }
        return socket
    }

    @Synchronized fun remove(socket: StateSocket) {
        socketPool[socket.route.address]?.remove(socket)
    }

    @Synchronized fun add(socket: StateSocket) {
        if (socketPool[socket.route.address] == null) {
            socketPool[socket.route.address] = mutableSetOf(socket)
        } else {
            socketPool[socket.route.address]!!.add(socket)
        }
    }
}