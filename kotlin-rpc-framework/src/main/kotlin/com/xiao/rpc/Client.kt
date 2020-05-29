package com.xiao.rpc

import com.xiao.rpc.handler.Chain

/**
 *
 * @author lix wang
 */
class Client {
    class Call(private val request: Request) {
        fun execute(): Response {
            val chain = Chain(request)
            chain.initHandlers()
            chain.refreshContext()
            return chain.execute()
        }
    }

    fun newCall(request: Request): Call {
        return Call(request)
    }
}