package com.xiao.rpc.handler

import com.xiao.base.annotation.Log
import com.xiao.base.logging.Logging
import com.xiao.rpc.Constants
import com.xiao.rpc.helper.ConnectionHelper
import com.xiao.rpc.helper.RouteHelper
import com.xiao.rpc.io.Response

/**
 *
 * @author lix wang
 */
class ConnectionHandler(override val chain: Chain) : Handler {
    override fun handle(): Response {
        log.info("Entered ${this::class.java.simpleName}.")
        val startTime = System.currentTimeMillis()
        val routes = chain.exchange.routes ?: RouteHelper.findRoutes(chain.client, chain.exchange.address)
        check(routes.isNotEmpty()) {
            "ConnectionHandler can not find valid routes."
        }
        val connection = ConnectionHelper.findConnection(chain.client, routes, chain.exchange.connectTimeout)
        check(connection != null) {
            "ConnectionHandler can not find valid connection."
        }
        chain.exchange.connection = connection
        val endTime = System.currentTimeMillis()
        println("*** ConnectionHandler cost: ${endTime - startTime} ms")
        return chain.execute()
    }

    @Log(Constants.RPC_LOGGER)
    companion object : Logging()
}