package com.xiao.rpc.handler

import com.xiao.rpc.Address
import com.xiao.rpc.Client
import com.xiao.rpc.factory.ChainHandlerFactorySelector
import com.xiao.rpc.helper.RpcHelper
import com.xiao.rpc.io.Exchange
import com.xiao.rpc.io.Request
import com.xiao.rpc.io.Response

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

    fun execute(): Response {
        if (index > handlers.size) {
            throw IndexOutOfBoundsException("Chain handler execute out of bounds.")
        }
        @Suppress("USELESS_ELVIS")
        val result = handlers[index++].handle() ?: throw IllegalStateException("Chain execute result must not null.")
        afterExecute()
        return result
    }

    init {
        prepareExchange()
    }

    private fun prepareExchange() {
        this.exchange = Exchange().apply {
            this.address = createAddress(request)
            this.connectTimeout = client.connectTimeout
            this.readTimeout = client.readTimeout
            this.writeTimeout = client.writeTimeout
        }
    }

    private fun createAddress(request: Request): Address {
        return Address(request.host()!!, request.scheme()!!, request.port())
    }

    private fun afterExecute() {
        RpcHelper.deleteRpc()
    }
}