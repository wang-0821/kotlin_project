package com.xiao.rpc.handler

import com.xiao.rpc.protocol.Response

/**
 *
 * @author lix wang
 */
class CacheHandler(override val chain: Chain) : Handler {
    override fun handle(): Response {
        return Response()
    }
}