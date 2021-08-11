package xiao.http.context

import xiao.base.logging.Logging
import xiao.beans.context.Context
import xiao.http.Cleaner
import xiao.http.Route
import xiao.http.annotation.AutoClean
import xiao.http.annotation.ClientContext
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

    private var routePool = ConcurrentHashMap<xiao.http.Address, MutableList<Route>>()

    fun get(address: xiao.http.Address): List<Route>? {
        synchronized(routePool) {
            return routePool[address]?.filter { it.tryUse() }
        }
    }

    fun add(address: xiao.http.Address, routes: List<Route>): Boolean {
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

    fun add(address: xiao.http.Address, route: Route): Boolean {
        return add(address, listOf(route))
    }

    fun remove(route: Route): Boolean {
        return routePool[route.address]?.remove(route) ?: false
    }

    private fun isFull(address: xiao.http.Address): Boolean {
        val pooledSize = routePool[address]?.size ?: 0
        return contextConfig.singleCorePoolSize <= pooledSize
    }

    override fun cleanup() {
        var cleaned = 0
        val emptyAddress = mutableListOf<xiao.http.Address>()
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

    private fun removeEntries(addresses: List<xiao.http.Address>) {
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