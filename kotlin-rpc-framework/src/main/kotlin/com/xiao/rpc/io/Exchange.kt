package com.xiao.rpc.io

import com.xiao.rpc.Address
import com.xiao.rpc.Route
import com.xiao.rpc.context.RouteContextAware

/**
 *
 * @author lix wang
 */
class Exchange : RouteContextAware {
    lateinit var address: Address
    var routes: Set<Route>? = null
    var connection: Connection? = null

    fun acquireRoutes(): Set<Route> {
        var result = routes ?: get(address)
        if (result.isNullOrEmpty()) {
            result = address.acquireRoutes()
        }
        return result
    }
}