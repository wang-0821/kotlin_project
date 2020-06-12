package com.xiao.rpc.handler

import com.xiao.base.exception.KtException
import com.xiao.rpc.Client
import com.xiao.rpc.Request
import com.xiao.rpc.Response
import com.xiao.rpc.exception.ExecuteException
import com.xiao.rpc.factory.ChainHandlerFactorySelector

/**
 *
 * @author lix wang
 */
class Chain(val client: Client, val request: Request) {
    private var handlers: List<Handler> = ChainHandlerFactorySelector.select().create(this)
    private var index = 0
    var exchange: Exchange? = null

    fun setHandlers(handlers: List<Handler>) {
        if (handlers.isNotEmpty()) {
            this.handlers = handlers
        }
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