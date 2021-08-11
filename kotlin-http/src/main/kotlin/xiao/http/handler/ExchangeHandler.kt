package xiao.http.handler

import xiao.http.io.Connection
import xiao.http.io.Exchange
import xiao.http.io.Request
import xiao.http.io.Response

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