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
        synchronized(routePool) {
            if (routePool[address] == null) {
                routePool[address] = mutableSetOf()
            }
            return routePool[address]!!.addAll(routes)
        }
    }

    fun add(address: Address, route: Route): Boolean {
        return add(address, setOf(route))
    }

    fun remove(route: Route): Boolean {
        return routePool[route.address]?.remove(route) ?: false
    }
}