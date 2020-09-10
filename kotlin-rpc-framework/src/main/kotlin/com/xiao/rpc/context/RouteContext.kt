package com.xiao.rpc.context

import com.xiao.base.context.Context
import com.xiao.rpc.Address
import com.xiao.rpc.Route
import com.xiao.rpc.annotation.ClientContext
import java.util.concurrent.ConcurrentHashMap

/**
 *
 * @author lix wang
 */
@ClientContext
class RouteContext(private val contextConfig: ClientContextConfig) : Context {
    companion object Key : Context.Key<RouteContext>
    override val key: Context.Key<RouteContext>
        get() = Key

    private var routePool = ConcurrentHashMap<Address, MutableList<Route>>()

    fun get(address: Address): List<Route>? {
        return routePool[address]
    }

    fun add(address: Address, routes: List<Route>): Boolean {
        synchronized(routePool) {
            if (isFull(address)) {
                return false
            }
            var list: MutableList<Route>? = null
            if (routePool[address] == null) {
                list = mutableListOf()
                routePool[address] = list
            }
            val realRoutes = routes.filter { !list!!.contains(it) }
            return list!!.addAll(realRoutes)
        }
    }

    fun add(address: Address, route: Route): Boolean {
        return add(address, listOf(route))
    }

    fun remove(route: Route): Boolean {
        return routePool[route.address]?.remove(route) ?: false
    }

    private fun isFull(address: Address): Boolean {
        val pooledSize = routePool[address]?.size ?: 0
        return contextConfig.singleCorePoolSize <= pooledSize
    }
}