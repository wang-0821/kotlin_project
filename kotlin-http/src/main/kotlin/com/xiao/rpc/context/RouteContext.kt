package com.xiao.rpc.context

import com.xiao.base.logging.Logging
import com.xiao.beans.context.Context
import com.xiao.rpc.Address
import com.xiao.rpc.Cleaner
import com.xiao.rpc.Route
import com.xiao.rpc.annotation.AutoClean
import com.xiao.rpc.annotation.ClientContext
import java.util.concurrent.ConcurrentHashMap

/**
 *
 * @author lix wang
 */
@AutoClean
@ClientContext
class RouteContext(private val contextConfig: ClientContextConfig) : Context, Cleaner {
    companion object Key : Context.Key<RouteContext>, Logging()
    override val key: Context.Key<RouteContext>
        get() = Key

    private var routePool = ConcurrentHashMap<Address, MutableList<Route>>()

    fun get(address: Address): List<Route>? {
        synchronized(routePool) {
            return routePool[address]?.filter { it.tryUse() }
        }
    }

    fun add(address: Address, routes: List<Route>): Boolean {
        synchronized(routePool) {
            if (isFull(address)) {
                return false
            }
            var list: MutableList<Route>? = routePool[address]
            if (list == null) {
                list = mutableListOf()
                routePool[address] = list
            }
            val realRoutes = routes.filter { !list.contains(it) }
            return list.addAll(realRoutes)
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

    override fun cleanup() {
        var cleaned = 0
        val emptyAddress = mutableListOf<Address>()
        routePool.entries.forEach { entry ->
            entry.value.forEach { route ->
                if (route.tryClose(contextConfig.timeUnit.toMillis(contextConfig.idleTimeout))) {
                    entry.value.remove(route)
                    cleaned++
                    if (entry.value.isEmpty()) {
                        emptyAddress.add(entry.key)
                    }
                }
            }
        }
        if (emptyAddress.isNotEmpty()) {
            removeEntries(emptyAddress)
        }
        log.debug("Cleaned $cleaned routes.")
    }

    private fun removeEntries(addresses: List<Address>) {
        synchronized(routePool) {
            addresses.forEach {
                val routes = routePool[it]
                if (routes != null && routes.isEmpty()) {
                    routePool.remove(it)
                }
            }
        }
    }
}