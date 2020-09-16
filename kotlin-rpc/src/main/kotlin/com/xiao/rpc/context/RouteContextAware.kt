package com.xiao.rpc.context

import com.xiao.base.context.Context
import com.xiao.rpc.Address
import com.xiao.rpc.Route

/**
 *
 * @author lix wang
 */
interface RouteContextAware : ClientContextPoolAware {
    fun getRoute(contextPoolKey: Context.Key<*>, address: Address): List<Route>? {
        return context(contextPoolKey)?.get(address)
    }

    fun addRoutes(contextPoolKey: Context.Key<*>, address: Address, routes: List<Route>): Boolean {
        return context(contextPoolKey)?.add(address, routes) ?: false
    }

    fun addRoute(contextPoolKey: Context.Key<*>, address: Address, route: Route): Boolean {
        return context(contextPoolKey)?.add(address, route) ?: false
    }

    fun removeRoute(contextPoolKey: Context.Key<*>, route: Route): Boolean {
        return context(contextPoolKey)?.remove(route) ?: false
    }

    private fun context(contextPoolKey: Context.Key<*>): RouteContext? {
        return getContext(contextPoolKey, KEY) as RouteContext?
    }

    companion object {
        val KEY = RouteContext.Key
    }
}