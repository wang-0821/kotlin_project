package com.xiao.rpc.factory

import com.xiao.base.context.BeanRegistryAware
import com.xiao.rpc.Route
import sun.security.ssl.SSLSocketFactoryImpl
import java.net.Socket
import javax.net.ssl.SSLSocket

/**
 *
 * @author lix wang
 */
interface SSLSocketFactory {
    fun createSSLSocket(socket: Socket, route: Route): SSLSocket
}

object DefaultSSLSocketFactory : SSLSocketFactory {
    private val factory = SSLSocketFactoryImpl()

    override fun createSSLSocket(socket: Socket, route: Route): SSLSocket {
        // true means autoClose
        return factory.createSocket(socket, route.address.host, route.address.port, true) as SSLSocket
    }
}

object SSLSocketFactorySelector : BeanRegistryAware {
    fun select(): SSLSocketFactory {
        return getByType(SSLSocketFactory::class.java) ?: DefaultSSLSocketFactory
    }
}
