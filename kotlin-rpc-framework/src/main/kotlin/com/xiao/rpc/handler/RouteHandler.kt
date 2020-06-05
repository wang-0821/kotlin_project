package com.xiao.rpc.handler

import com.xiao.base.context.ContextAware
import com.xiao.rpc.Response
import com.xiao.rpc.RouteContext
import com.xiao.rpc.acquireRoutes
import com.xiao.rpc.exception.RouteException
import com.xiao.rpc.io.Exchange

/**
 *
 * @author lix wang
 */
class RouteHandler(override val chain: Chain) : Handler, ContextAware {
    override fun handle(): Response {
        val routeContext = get(RouteContext.Key)
        var routes = routeContext?.get(chain.request.address)
        if (routes.isNullOrEmpty()) {
            routes = chain.request.address.acquireRoutes()
            routeContext?.add(chain.request.address, routes)
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