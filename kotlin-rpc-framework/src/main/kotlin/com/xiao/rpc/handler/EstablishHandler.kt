package com.xiao.rpc.handler

import com.xiao.rpc.ContextAware
import com.xiao.rpc.Response
import com.xiao.rpc.RouteContext

/**
 *
 * @author lix wang
 */
class EstablishHandler(override val chain: Chain) : Handler, ContextAware {
    override fun handle(): Response {
        val routeContext = get(RouteContext.Key)
        val routes = routeContext?.get(chain.request.address)
        // todo implements handle
        return Response()
    }
}