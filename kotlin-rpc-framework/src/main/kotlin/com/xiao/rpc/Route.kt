package com.xiao.rpc

import com.xiao.base.exception.KtException
import com.xiao.rpc.exception.DnsException
import java.net.InetAddress
import java.net.InetSocketAddress

/**
 *
 * @author lix wang
 */
class Route(val address: Address, val inetSocketAddress: InetSocketAddress)

@Throws(KtException::class)
fun Address.acquireRoutes(): Set<Route> {
    try {
        return InetAddress.getAllByName(this.host).asSequence()
            .map { InetSocketAddress(it, this.port) }
            .map { Route(this, it) }.toSet()
    } catch (e: Exception) {
        throw DnsException.dnsDomainResolveException("failed address: $this")
    }
}