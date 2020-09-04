package com.xiao.rpc.handler

import com.xiao.rpc.helper.RouteHelper
import com.xiao.rpc.io.Response
import org.slf4j.LoggerFactory

/**
 *
 * @author lix wang
 */
class RouteHandler(override val chain: Chain) : Handler {
    private val log = LoggerFactory.getLogger(RouteHelper::class.java)

    override fun handle(): Response {
        log.info("Start route handler.")
        val startTime = System.currentTimeMillis()
        val routes = RouteHelper.findRoutes(chain.client, chain.exchange.address)
        check(routes.isNotEmpty()) {
            "RouteHandler can not find valid routes."
        }
        chain.exchange.routes = routes
        val endTime = System.currentTimeMillis()
        println("*** RouteHandler cost: ${endTime - startTime} ms")
        return chain.execute()
    }
}