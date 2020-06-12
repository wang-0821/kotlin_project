package com.xiao.rpc.factory

import com.xiao.base.context.BeanRegistryAware
import com.xiao.rpc.ProtocolType
import com.xiao.rpc.StateSocket
import com.xiao.rpc.io.Connection
import com.xiao.rpc.io.HttpConnection
import com.xiao.rpc.io.HttpsConnection

/**
 *
 * @author lix wang
 */
interface ConnectionFactory {
    fun create(socket: StateSocket): Connection
}

object DefaultConnectionFactory : ConnectionFactory {
    override fun create(socket: StateSocket): Connection {
        return when (socket.route.address.protocol) {
            ProtocolType.HTTP -> HttpConnection(socket)
            ProtocolType.HTTPS -> HttpsConnection(socket)
        }
    }
}

/**
 * You can register a [ConnectionFactory] bean to replace [DefaultConnectionFactory]
 */
object ConnectionFactorySelector : BeanRegistryAware {
    fun select(): ConnectionFactory {
        return getByType(ConnectionFactory::class.java) ?: DefaultConnectionFactory
    }
}