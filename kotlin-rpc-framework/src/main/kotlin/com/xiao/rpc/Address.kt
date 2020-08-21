package com.xiao.rpc

import com.xiao.base.exception.KtException
import com.xiao.rpc.exception.DnsException
import java.net.InetAddress
import java.net.InetSocketAddress

/**
 *
 * @author lix wang
 */
class Address(val host: String, scheme: String) {
    val isTls = scheme == "https"

    var port: Int = -1
        get() {
            return if (field > 0) {
                field
            } else {
                if (isTls) {
                    443
                } else {
                    80
                }
            }
        }

    @Throws(KtException::class)
    fun acquireRoutes(): Set<Route> {
        try {
            return InetAddress.getAllByName(this.host).asSequence()
                .map { InetSocketAddress(it, this.port) }
                .map { Route(this, it) }.toSet()
        } catch (e: Exception) {
            throw DnsException.dnsDomainResolveException("failed address: $this")
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Address

        if (host != other.host || port != other.port) return false

        return true
    }

    override fun hashCode(): Int {
        var result = host.hashCode()
        result = 31 * result + port
        return result
    }
}