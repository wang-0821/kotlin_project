package com.xiao.rpc

import com.xiao.rpc.factory.SocketFactorySelector
import java.io.IOException
import java.net.InetSocketAddress

/**
 *
 * @author lix wang
 */
class Route(val address: Address, private val inetSocketAddress: InetSocketAddress) {
    @Throws(IOException::class)
    fun acquireSocket(timeout: Int = -1): StateSocket {
        val socketFactory = SocketFactorySelector.select()
        val socket = socketFactory.createSocket(this)
        socket.connect(socket.route.inetSocketAddress, timeout)
        return socket
    }
}