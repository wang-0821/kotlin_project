package com.xiao.rpc.handler

import com.xiao.rpc.Address
import com.xiao.rpc.Route
import com.xiao.rpc.StateSocket
import com.xiao.rpc.context.RouteContextAware
import com.xiao.rpc.io.Connection

/**
 *
 * @author lix wang
 */
class Exchange : RouteContextAware {
    lateinit var address: Address
    var routes: Set<Route>? = null
    var socket: StateSocket? = null
    var connection: Connection? = null

    fun acquireRoutes(): Set<Route> {
        var result = routes ?: get(address)
        if (result.isNullOrEmpty()) {
            result = address.acquireRoutes()
        }
        return result
    }
}