package com.xiao.rpc

/**
 *
 * @author lix wang
 */
interface SocketFactory {
    fun createSocket(route: Route): StateSocket
}

object DefaultSocketFactory : SocketFactory {
    override fun createSocket(route: Route): StateSocket {
        return StateSocket(route)
    }
}