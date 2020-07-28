package com.xiao.rpc.factory

import com.xiao.base.context.BeanRegistryAware
import com.xiao.rpc.StateSocket
import com.xiao.rpc.exception.ConnectionException
import com.xiao.rpc.io.Connection

/**
 *
 * @author lix wang
 */
interface ConnectionFactory {
    @Throws(ConnectionException::class)
    fun create(socket: StateSocket): Connection
}

object DefaultConnectionFactory : ConnectionFactory {
    override fun create(socket: StateSocket): Connection {
        try {
            val result = Connection(socket.route, socket)
            // todo
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