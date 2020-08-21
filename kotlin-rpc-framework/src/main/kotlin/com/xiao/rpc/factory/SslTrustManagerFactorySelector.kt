package com.xiao.rpc.factory

import java.security.KeyStore
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

/**
 *
 * @author lix wang
 */
object SslTrustManagerFactorySelector : AbstractSelector<SslTrustManagerFactory>() {
    override fun selectDefault(): SslTrustManagerFactory {
        return object : SslTrustManagerFactory {
            private var trustManagerFactory: TrustManagerFactory? = null
            override fun trustManager(): X509TrustManager {
                if (trustManagerFactory == null) {
                    createTrustManager()
                }
                return trustManagerFactory!!.trustManagers[0] as X509TrustManager
            }

            fun createTrustManager() {
                if (trustManagerFactory != null) {
                    return
                }
                val factory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
                factory.init(null as KeyStore?)
                trustManagerFactory = factory
            }
        }
    }
}