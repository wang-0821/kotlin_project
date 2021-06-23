package com.xiao.rpc.factory

import com.xiao.rpc.Route
import java.net.Socket
import javax.net.ssl.SSLSocket

/**
 *
 * @author lix wang
 */
@FunctionalInterface
interface SslSocketFactory {
    fun createSSLSocket(socket: Socket, route: Route): SSLSocket
}