package com.xiao.rpc.factory

import com.xiao.rpc.Protocol
import com.xiao.rpc.StateSocket
import com.xiao.rpc.io.Connection
import com.xiao.rpc.io.HttpConnection

/**
 *
 * @author lix wang
 */
object ConnectionFactorySelector : AbstractSelector<ConnectionFactory>() {
    override fun selectDefault(): ConnectionFactory {
        return object : ConnectionFactory {
            override fun create(socket: StateSocket, protocol: Protocol): Connection {
                try {
                    val result = HttpConnection(socket.route, socket)
                    result.connect()
                    return result
                } catch (e : Exception) {
                    throw IllegalStateException("ConnectionFactory create Connection failed.")
                }
            }
        }
    }
}