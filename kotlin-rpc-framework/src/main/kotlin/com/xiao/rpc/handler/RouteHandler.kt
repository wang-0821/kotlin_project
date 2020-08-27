package com.xiao.rpc.handler

import com.xiao.rpc.context.RouteContextAware
import com.xiao.rpc.io.Response

/**
 *
 * @author lix wang
 */
class RouteHandler(override val chain: Chain) : Handler, RouteContextAware {
    override fun handle(): Response {
        val startTime = System.currentTimeMillis()
        var routes = get(chain.exchange.address)
        if (routes.isNullOrEmpty()) {
            routes = chain.exchange.address.acquireRoutes()
            chain.exchange.routes = routes
            add(chain.exchange.address, routes)
        }
        if (routes.isNullOrEmpty()) {
            throw NoSuchElementException("RouteHandler have no valid route.")
        }
        val endTime = System.currentTimeMillis()
        println("*** RouteHandler cost: ${endTime - startTime} ms")
        return chain.execute()
    }
}