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
    lateinit var rpcUuid: String
    var connectTimeout: Int = -1
    var readTimeout: Int = -1
    var writeTimeout: Int = -1

    fun acquireRoutes(): Set<Route> {
        var result = routes ?: get(address)
        if (result.isNullOrEmpty()) {
            result = address.acquireRoutes()
        }
        return result
    }
}