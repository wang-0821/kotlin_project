package com.xiao.rpc

import java.net.InetSocketAddress

/**
 *
 * @author lix wang
 */
class Route(
    val address: Address,
    val inetSocketAddress: InetSocketAddress
) : CloseableResource {
    override fun tryClose(keepAliveMills: Int): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun tryUse(): Boolean {
        return true
    }

    override fun unUse(): Boolean {
        return true
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