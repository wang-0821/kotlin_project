package com.xiao.rpc.factory

import com.xiao.rpc.Route
import java.net.Socket
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory

/**
 *
 * @author lix wang
 */
object SslSocketFactorySelector : AbstractSelector<SslSocketFactory>() {
    override fun selectDefault(): SslSocketFactory {
        return object : SslSocketFactory {
            private var factory: SSLSocketFactory? = null

            override fun createSSLSocket(socket: Socket, route: Route): SSLSocket {
                if (factory == null) {
                    createSslSocketFactory()
                }
                // true means autoClose
                return factory!!.createSocket(socket, route.address.host, route.address.port, true) as SSLSocket
            }

            private fun createSslSocketFactory() {
                if (factory != null) {
                    return
                }
                val sslContext = SSLContext.getInstance("TLS")
                sslContext.init(null, arrayOf(SslTrustManagerFactorySelector.select().trustManager()), null)
                factory = sslContext.socketFactory
            }
        }
    }
}