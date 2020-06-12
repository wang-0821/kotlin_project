package com.xiao.rpc.context

import com.xiao.base.annotation.ContextInject
import com.xiao.base.context.AbstractContext
import com.xiao.base.context.Context
import com.xiao.rpc.Address
import com.xiao.rpc.StateSocket
import com.xiao.rpc.validate
import java.util.concurrent.ConcurrentHashMap

/**
 *
 * @author lix wang
 */
@ContextInject
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

    @Synchronized fun remove(socket: StateSocket): Boolean {
        return socketPool[socket.route.address]?.remove(socket) ?: false
    }

    @Synchronized fun add(socket: StateSocket): Boolean {
        return if (socketPool[socket.route.address] == null) {
            socketPool[socket.route.address] = mutableSetOf(socket)
            true
        } else {
            socketPool[socket.route.address]!!.add(socket)
        }
    }
}