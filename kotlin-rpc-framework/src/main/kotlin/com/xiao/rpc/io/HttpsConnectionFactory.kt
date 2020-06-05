package com.xiao.rpc.io

import com.xiao.rpc.ProtocolType
import com.xiao.rpc.StateSocket

/**
 *
 * @author lix wang
 */
object HttpsConnectionFactory : ConnectionFactory {
    override fun create(socket: StateSocket): Connection {
        return HttpsConnection(socket)
    }

    override val protocol: ProtocolType = ProtocolType.HTTPS
}