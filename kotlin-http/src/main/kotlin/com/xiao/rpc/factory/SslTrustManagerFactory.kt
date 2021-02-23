package com.xiao.rpc.factory

import javax.net.ssl.X509TrustManager

/**
 *
 * @author lix wang
 */
interface SslTrustManagerFactory {
    fun trustManager(): X509TrustManager
}