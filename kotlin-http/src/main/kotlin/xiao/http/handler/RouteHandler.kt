package xiao.http.handler

import xiao.http.helper.RouteHelper
import xiao.http.io.Response

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
}