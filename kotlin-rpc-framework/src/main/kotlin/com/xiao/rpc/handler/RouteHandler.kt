package com.xiao.rpc.handler

import com.xiao.rpc.Response
import com.xiao.rpc.context.RouteContextAware
import com.xiao.rpc.exception.RouteException

/**
 *
 * @author lix wang
 */
class RouteHandler(override val chain: Chain) : Handler, RouteContextAware {
    override fun handle(): Response {
        var routes = get(chain.exchange.address)
        if (routes.isNullOrEmpty()) {
            routes = chain.exchange.address.acquireRoutes()
            chain.exchange.routes = routes
            add(chain.exchange.address, routes)
        }
        if (routes.isNullOrEmpty()) {
            throw RouteException.noAvailableRoutes("address: ${chain.exchange.address} ")
        }
        return chain.execute()
    }
}