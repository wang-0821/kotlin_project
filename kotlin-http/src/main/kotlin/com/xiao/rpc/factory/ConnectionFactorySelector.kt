package com.xiao.rpc.factory

import com.xiao.rpc.Protocol
import com.xiao.rpc.Route
import com.xiao.rpc.io.Connection
import com.xiao.rpc.io.HttpConnection
import java.net.Socket

/**
 *
 * @author lix wang
 */
object ConnectionFactorySelector : AbstractSelector<ConnectionFactory>() {
    override fun selectDefault(): ConnectionFactory {
        return object : ConnectionFactory {
            override fun create(socket: Socket, route: Route, protocol: Protocol): Connection {
                val result = HttpConnection(route, socket, null)
                result.connect()
                return result
            }
        }
    }
}