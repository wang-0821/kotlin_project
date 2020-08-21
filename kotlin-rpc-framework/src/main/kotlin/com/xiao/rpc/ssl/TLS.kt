package com.xiao.rpc.ssl

import javax.net.ssl.SSLContext

/**
 *
 * @author lix wang
 */
class TLS {
    fun getSSLContext(): SSLContext {
        return SSLContext.getInstance("TLS")
    }
}