package com.xiao.rpc.handler

import com.xiao.rpc.io.Connection
import com.xiao.rpc.io.Exchange
import com.xiao.rpc.io.Request
import com.xiao.rpc.io.Response
import org.slf4j.LoggerFactory

/**
 *
 * @author lix wang
 */
class ExchangeHandler(override val chain: Chain) : Handler {
    private val log = LoggerFactory.getLogger(ExchangeHandler::class.java)

    override fun handle(): Response {
        log.info("Start exchange handler.")
        check(chain.exchange.connection != null) {
            "ExchangeHandler can not find valid connection."
        }
        return doRequest(chain.exchange.connection!!, chain.request, chain.exchange)
    }

    private fun doRequest(connection: Connection, request: Request, exchange: Exchange): Response {
        val startTime = System.currentTimeMillis()
        connection.writeHeaders(request)
        connection.writeBody(request)
        connection.finishRequest()
        val result = connection.response(exchange)
        val endTime = System.currentTimeMillis()
        println("***** ExechangeHandler cost: ${endTime - startTime} ms")
        return result
    }
}