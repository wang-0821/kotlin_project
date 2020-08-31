package com.xiao.rpc.factory

import com.xiao.rpc.Protocol
import com.xiao.rpc.StateSocket
import com.xiao.rpc.io.Connection
import com.xiao.rpc.io.Exchange

/**
 *
 * @author lix wang
 */
interface ConnectionFactory {
    fun create(socket: StateSocket, protocol: Protocol = Protocol.HTTP_1_1): Connection
}