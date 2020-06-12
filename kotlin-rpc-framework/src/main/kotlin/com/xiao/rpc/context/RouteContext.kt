package com.xiao.rpc.context

import com.xiao.base.annotation.ContextInject
import com.xiao.base.context.AbstractContext
import com.xiao.base.context.Context
import com.xiao.rpc.Address
import com.xiao.rpc.Route
import java.util.concurrent.ConcurrentHashMap

/**
 *
 * @author lix wang
 */
@ContextInject
class RouteContext : AbstractContext(RouteContext) {
    companion object Key : Context.Key<RouteContext>

    private var routePool = ConcurrentHashMap<Address, MutableSet<Route>>()

    fun get(address: Address): Set<Route>? {
        return routePool[address]
    }

    fun add(address: Address, routes: Set<Route>): Boolean {
        if (routePool[address] == null) {
            routePool[address] = mutableSetOf()
        }
        return routePool[address]!!.addAll(routes)
    }

    fun remove(route: Route) {
        routePool[route.address]?.remove(route)
    }
}