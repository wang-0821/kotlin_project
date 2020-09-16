package com.xiao.rpc.factory

import com.xiao.rpc.Route
import java.net.Socket

/**
 *
 * @author lix wang
 */
object SocketFactorySelector : AbstractSelector<SocketFactory>() {
    override fun selectDefault(): SocketFactory {
        return object : SocketFactory {
            override fun createSocket(route: Route, connectTimeout: Int): Socket {
                val socket = Socket()
                socket.connect(route.inetSocketAddress, connectTimeout)
                return socket
            }
        }
    }
}