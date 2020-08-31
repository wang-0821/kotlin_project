package com.xiao.rpc.context

import com.xiao.base.context.Context
import com.xiao.rpc.Address
import com.xiao.rpc.Route

/**
 *
 * @author lix wang
 */
interface RouteContextAware : ClientContextAware<RouteContext> {
    override val key: Context.Key<RouteContext>
        get() = RouteContext.Key

    fun get(clientContextKey: Context.Key<*>, address: Address): List<Route>? {
        return getContext(clientContextKey)?.get(address)
    }

    fun add(clientContextKey: Context.Key<*>, address: Address, routes: List<Route>): Boolean {
        return getContext(clientContextKey)?.add(address, routes) ?: false
    }

    fun add(clientContextKey: Context.Key<*>, address: Address, route: Route): Boolean {
        return getContext(clientContextKey)?.add(address, route) ?: false
    }

    fun remove(clientContextKey: Context.Key<*>, route: Route): Boolean {
        return getContext(clientContextKey)?.remove(route) ?: false
    }
}