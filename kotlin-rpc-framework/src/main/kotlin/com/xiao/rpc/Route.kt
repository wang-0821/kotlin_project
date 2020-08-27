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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Route

        if (address != other.address) return false
        if (inetSocketAddress != other.inetSocketAddress) return false

        return true
    }

    override fun hashCode(): Int {
        var result = address.hashCode()
        result = 31 * result + inetSocketAddress.hashCode()
        return result
    }
}