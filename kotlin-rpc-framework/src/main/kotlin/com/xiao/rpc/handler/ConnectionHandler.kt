package com.xiao.rpc.handler

import com.xiao.rpc.Response
import com.xiao.rpc.context.ConnectionContextAware

/**
 *
 * @author lix wang
 */
class ConnectionHandler(override val chain: Chain) : Handler, ConnectionContextAware {
    override fun handle(): Response {
        // todo
        return Response()
    }
}