package com.xiao.rpc

import com.xiao.rpc.factory.SocketFactorySelector
import java.io.IOException
import java.net.InetSocketAddress

/**
 *
 * @author lix wang
 */
class Route(val address: Address, val inetSocketAddress: InetSocketAddress)

@Throws(IOException::class)
fun Route.acquireSocket(timeout: Int = -1): StateSocket {
    val socketFactory = SocketFactorySelector.select()
    val socket = socketFactory.createSocket(this)
    if (timeout > 0) {
        socket.connect(socket.route.inetSocketAddress, timeout)
    } else {
        socket.connect(socket.route.inetSocketAddress)
    }
    return socket
}