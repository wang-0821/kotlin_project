package com.xiao.rpc.handler

import com.xiao.base.exception.KtException
import com.xiao.rpc.Address
import com.xiao.rpc.Client
import com.xiao.rpc.Request
import com.xiao.rpc.Response
import com.xiao.rpc.exception.ChainException
import com.xiao.rpc.factory.ChainHandlerFactorySelector

/**
 *
 * @author lix wang
 */
class Chain(val client: Client, val request: Request) {
    private var handlers: List<Handler> = ChainHandlerFactorySelector.select().create(this)
    private var index = 0
    lateinit var exchange: Exchange

    fun setHandlers(handlers: List<Handler>) {
        if (handlers.isNotEmpty()) {
            this.handlers = handlers
        }
    }

    @Throws(KtException::class)
    fun execute(): Response {
        if (index > handlers.size) {
            throw ChainException.executeOutOfBound()
        }
        @Suppress("USELESS_ELVIS")
        return handlers[index++].handle() ?: throw ChainException.noResponseError(request.toString())
    }

    init {
        prepareExchange()
    }

    private fun prepareExchange() {
        this.exchange = Exchange().apply {
            this.address = createAddress(request)
        }
    }

    private fun createAddress(request: Request): Address {
        return Address(request.host(), request.scheme()).apply {
            this.port = request.port()
        }
    }
}