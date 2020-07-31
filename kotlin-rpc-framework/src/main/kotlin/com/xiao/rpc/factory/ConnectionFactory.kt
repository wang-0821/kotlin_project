package com.xiao.rpc.factory

import com.xiao.base.context.BeanRegistryAware
import com.xiao.rpc.Protocol
import com.xiao.rpc.StateSocket
import com.xiao.rpc.exception.ConnectionException
import com.xiao.rpc.io.Connection
import com.xiao.rpc.io.Http1Connection
import com.xiao.rpc.io.Http2Connection

/**
 *
 * @author lix wang
 */
interface ConnectionFactory {
    @Throws(ConnectionException::class)
    fun create(socket: StateSocket, protocol: Protocol = Protocol.HTTP_1_1): Connection
}

object DefaultConnectionFactory : ConnectionFactory {
    override fun create(socket: StateSocket, protocol: Protocol): Connection {
        try {
            val result = if (protocol == Protocol.HTTP_2) {
                Http2Connection()
            } else {
                Http1Connection(socket.route, socket)
            }
            result.connect()
            return result
        } catch (e : Exception) {
            throw ConnectionException.noAvailableConnection()
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