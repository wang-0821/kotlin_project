package com.xiao.rpc.handler

import com.xiao.rpc.exception.ConnectionException
import com.xiao.rpc.io.Connection
import com.xiao.rpc.io.Request
import com.xiao.rpc.io.Response

/**
 *
 * @author lix wang
 */
class ExchangeHandler(override val chain: Chain) : Handler {
    override fun handle(): Response {
        if (chain.exchange.connection == null) {
            throw ConnectionException.noAvailableConnection("${this.javaClass.simpleName} have no connection.")
        }

        return doRequest(chain.exchange.connection!!, chain.request)
    }

    private fun doRequest(connection: Connection, request: Request): Response {
        connection.writeHeaders(request)
        connection.writeBody(request)
        connection.finishRequest()
        return connection.response()
    }
}