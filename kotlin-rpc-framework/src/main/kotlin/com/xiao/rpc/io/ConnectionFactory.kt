package com.xiao.rpc.io

import com.xiao.base.context.BeanRegistryAware
import com.xiao.rpc.ProtocolType
import com.xiao.rpc.StateSocket
import java.io.IOException

/**
 *
 * @author lix wang
 */
interface ConnectionFactory {
    @Throws(IOException::class)
    fun create(socket: StateSocket): Connection

    val protocol: ProtocolType
}

object ConnectionSelector : BeanRegistryAware {
    fun select(protocol: ProtocolType): ConnectionFactory {
        return when (protocol) {
            ProtocolType.HTTP -> getByType(HttpConnectionFactory::class.java) ?: HttpConnectionFactory
            ProtocolType.HTTPS -> getByType(HttpsConnectionFactory::class.java) ?: HttpsConnectionFactory
        }
    }
}