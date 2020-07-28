package com.xiao.rpc.handler

import com.xiao.base.exception.KtException
import com.xiao.rpc.Response
import com.xiao.rpc.Route
import com.xiao.rpc.StateSocket
import com.xiao.rpc.context.RouteContextAware
import com.xiao.rpc.context.SocketContextAware
import com.xiao.rpc.exception.SocketException

/**
 *
 * @author lix wang
 */
class SocketHandler(override val chain: Chain)
    : Handler, SocketContextAware, RouteContextAware {
    override fun handle(): Response {
        createSocket(chain)
        return chain.execute()
    }

    @Throws(KtException::class)
    private fun createSocket(chain: Chain) {
        val routes = chain.exchange.acquireRoutes()
        if (chain.exchange.socket != null) {
            return
        }

        createWithCache(routes)

        if (chain.exchange.socket == null) {
            createSocket(routes)
        }

        if (chain.exchange.socket == null) {
            throw SocketException.connectFailed(chain.exchange.address.toString())
        }
    }

    private fun createSocket(routes: Set<Route>) {
        for(route in routes) {
            var socket: StateSocket? = null
            try {
                socket = route.acquireSocket(chain.client.connectTimeout)
                chain.exchange.socket = socket
            } catch (e: Exception) {
                chain.exchange.socket = null
            }
            if (chain.exchange.socket != null) {
                add(socket!!)
                add(chain.exchange.address, route)
                break
            }
        }
    }

    private fun createWithCache(routes: Set<Route>) {
        if (routes.isNullOrEmpty()) {
            return
        }
        for (route in routes) {
            val socket = poll(route)
            socket?.let {
                chain.exchange.socket = it
            }

            if (chain.exchange.socket != null) {
                break
            }
        }
    }
}