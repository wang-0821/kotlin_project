package xiao.http.context

import xiao.beans.context.Context
import xiao.http.Route

/**
 *
 * @author lix wang
 */
interface RouteContextAware : ClientContextPoolAware {
    fun getRoute(contextPoolKey: Context.Key<*>, address: xiao.http.Address): List<Route>? {
        return context(contextPoolKey)?.get(address)
    }

    fun addRoutes(contextPoolKey: Context.Key<*>, address: xiao.http.Address, routes: List<Route>): Boolean {
        return context(contextPoolKey)?.add(address, routes) ?: false
    }

    fun addRoute(contextPoolKey: Context.Key<*>, address: xiao.http.Address, route: Route): Boolean {
        return context(contextPoolKey)?.add(address, route) ?: false
    }

    fun removeRoute(contextPoolKey: Context.Key<*>, route: Route): Boolean {
        return context(contextPoolKey)?.remove(route) ?: false
    }

    private fun context(contextPoolKey: Context.Key<*>): RouteContext? {
        return getContext(contextPoolKey, KEY) as RouteContext?
    }

    companion object {
        @JvmField
        val KEY = RouteContext.Key
    }
}