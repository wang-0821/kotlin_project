package xiao.http.handler

import xiao.http.Client
import xiao.http.factory.ChainHandlerFactorySelector
import xiao.http.io.Exchange
import xiao.http.io.Request
import xiao.http.io.Response

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
        return handlers[index++].handle() ?: throw IllegalStateException("Chain execute result must not null.")
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

    private fun createAddress(request: Request): xiao.http.Address {
        return xiao.http.Address(request.host()!!, request.scheme()!!, request.port())
    }
}