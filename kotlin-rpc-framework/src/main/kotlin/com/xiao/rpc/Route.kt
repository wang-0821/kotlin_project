package com.xiao.rpc

import com.xiao.rpc.cleaner.Cleaner
import java.net.InetSocketAddress

/**
 *
 * @author lix wang
 */
class Route(val address: Address, val inetSocketAddress: InetSocketAddress) : Cleaner {
    override fun cleanup() {
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