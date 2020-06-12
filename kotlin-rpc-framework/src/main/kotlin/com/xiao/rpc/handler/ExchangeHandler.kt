package com.xiao.rpc.handler

import com.xiao.rpc.Response

/**
 *
 * @author lix wang
 */
class ExchangeHandler(override val chain: Chain) : Handler {
    override fun handle(): Response {
        // todo implement real call exchange
        return Response()
    }
}