package com.xiao.rpc.handler

import com.xiao.rpc.helper.ConnectionHelper
import com.xiao.rpc.helper.RouteHelper
import com.xiao.rpc.io.Response

/**
 *
 * @author lix wang
 */
class ConnectionHandler(override val chain: Chain) : Handler {
    override fun handle(): Response {
        val routes = chain.exchange.routes ?: RouteHelper.findRoutes(chain.client, chain.exchange.address)
        check(routes.isNotEmpty()) {
            "ConnectionHandler can not find valid routes."
        }
        val connection = ConnectionHelper.findConnection(chain.client, routes, chain.exchange.connectTimeout)
        check(connection != null) {
            "ConnectionHandler can not find valid connection."
        }
        connection.readTimeout(chain.exchange.readTimeout)
        chain.exchange.connection = connection
        return chain.execute()
    }
}