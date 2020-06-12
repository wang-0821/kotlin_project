package com.xiao.rpc.handler

import com.xiao.rpc.Response
import com.xiao.rpc.acquireRoutes
import com.xiao.rpc.context.RouteContext
import com.xiao.rpc.context.RouteContextAware
import com.xiao.rpc.exception.RouteException

/**
 *
 * @author lix wang
 */
class RouteHandler(override val chain: Chain) : Handler, RouteContextAware {
    override fun handle(): Response {
        var routes = get(chain.request.address)
        if (routes.isNullOrEmpty()) {
            routes = chain.request.address.acquireRoutes()
            add(chain.request.address, routes)
        }
        if (routes.isNullOrEmpty()) {
            throw RouteException.noAvailableRoutes("address: ${chain.request.address} ")
        }
        chain.exchange ?: run {
            chain.exchange = Exchange()
        }
        chain.exchange!!.routes = routes
        return chain.execute()
    }
}