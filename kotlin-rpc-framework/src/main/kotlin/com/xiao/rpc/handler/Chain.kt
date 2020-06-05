package com.xiao.rpc.handler

import com.xiao.base.exception.KtException
import com.xiao.rpc.Client
import com.xiao.rpc.Request
import com.xiao.rpc.Response
import com.xiao.rpc.RouteContext
import com.xiao.rpc.exception.ExecuteException
import com.xiao.rpc.io.Exchange

/**
 *
 * @author lix wang
 */
class Chain(val client: Client, val request: Request) {
    private var handlers: MutableList<Handler> = mutableListOf()
    private var index = 0
    var exchange: Exchange? = null

    fun initContext() {
        RouteContext()
    }

    fun initHandlers() {
        handlers.add(RouteHandler(this))
    }

    @Throws(KtException::class)
    fun execute(): Response {
        if (index > handlers.size) {
            throw ExecuteException.executeOutOfBound()
        }
        @Suppress("USELESS_ELVIS")
        return handlers[index++].handle() ?: throw ExecuteException.noResponseError(request.toString())
    }
}