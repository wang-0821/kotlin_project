package com.xiao.rpc.factory

import com.xiao.rpc.Route
import com.xiao.rpc.StateSocket

/**
 *
 * @author lix wang
 */
object SocketFactorySelector : AbstractSelector<SocketFactory>() {
    override fun selectDefault(): SocketFactory {
        return object : SocketFactory {
            override fun createSocket(route: Route, connectTimeout: Int): StateSocket {
                val socket = StateSocket(route)
                socket.connect(route.inetSocketAddress, connectTimeout)
                return socket
            }
        }
    }
}