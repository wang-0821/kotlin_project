package com.xiao.rpc.io

import com.xiao.rpc.ProtocolType
import com.xiao.rpc.StateSocket

/**
 *
 * @author lix wang
 */
object HttpConnectionFactory : ConnectionFactory {
    override fun create(socket: StateSocket): Connection {
        return HttpConnection(socket)
    }

    override val protocol: ProtocolType = ProtocolType.HTTP
}