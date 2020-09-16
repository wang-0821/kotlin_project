package com.xiao.rpc.handler

import com.xiao.rpc.io.Connection
import com.xiao.rpc.io.Exchange
import com.xiao.rpc.io.Request
import com.xiao.rpc.io.Response

/**
 *
 * @author lix wang
 */
class ExchangeHandler(override val chain: Chain) : Handler {
    override fun handle(): Response {
        check(chain.exchange.connection != null) {
            "ExchangeHandler can not find valid connection."
        }
        return doRequest(chain.exchange.connection!!, chain.request, chain.exchange)
    }

    private fun doRequest(connection: Connection, request: Request, exchange: Exchange): Response {
        connection.writeHeaders(request)
        connection.writeBody(request)
        connection.finishRequest()
        return connection.response(exchange)
    }
}