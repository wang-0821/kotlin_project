package com.xiao.rpc.factory

import com.xiao.rpc.Protocol
import com.xiao.rpc.Route
import com.xiao.rpc.io.Connection
import java.net.Socket

/**
 *
 * @author lix wang
 */
interface ConnectionFactory {
    fun create(socket: Socket, route: Route, protocol: Protocol = Protocol.HTTP_1_1): Connection
}