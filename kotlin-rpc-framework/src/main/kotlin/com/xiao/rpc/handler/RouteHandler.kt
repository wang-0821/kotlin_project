package com.xiao.rpc.handler

import com.xiao.base.annotation.Log
import com.xiao.base.logging.Logging
import com.xiao.rpc.Constants
import com.xiao.rpc.helper.RouteHelper
import com.xiao.rpc.io.Response

/**
 *
 * @author lix wang
 */
class RouteHandler(override val chain: Chain) : Handler {
    override fun handle(): Response {
        val routes = RouteHelper.findRoutes(chain.client, chain.exchange.address)
        check(routes.isNotEmpty()) {
            "RouteHandler can not find valid routes."
        }
        chain.exchange.routes = routes
        return chain.execute()
    }

    @Log(Constants.RPC_LOGGER)
    companion object : Logging()
}