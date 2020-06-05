package com.xiao.rpc

import com.xiao.base.context.AbstractContext
import com.xiao.base.context.Context
import java.util.concurrent.ConcurrentHashMap

/**
 *
 * @author lix wang
 */
class RouteContext : AbstractContext(RouteContext) {
    companion object Key : Context.Key<RouteContext>

    private var routePool = ConcurrentHashMap<Address, MutableSet<Route>>()

    fun get(address: Address): Set<Route>? {
        return routePool[address]
    }

    fun add(address: Address, routes: Set<Route>) {
        if (routePool[address] == null) {
            routePool[address] = mutableSetOf()
        }
        routePool[address]!!.addAll(routes)
    }

    fun remove(route: Route) {
        routePool[route.address]?.remove(route)
    }
}