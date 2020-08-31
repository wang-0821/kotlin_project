package com.xiao.rpc.helper

import com.xiao.base.context.Context
import com.xiao.rpc.Client
import com.xiao.rpc.Route
import com.xiao.rpc.StateSocket
import com.xiao.rpc.context.SocketContextAware
import com.xiao.rpc.factory.SocketFactorySelector

/**
 *
 * @author lix wang
 */
object SocketHelper : SocketContextAware {
    fun findSocket(client: Client, routes: List<Route>, connectTimeout: Int): StateSocket? {
        if (client.clientContextPool != null) {
            var socket = getSocketWithCache(client.clientContextPool!!.key, routes)
            if (socket != null) {
                return socket
            }
            socket = createSocket(routes, connectTimeout)
            if (socket != null) {
                add(client.clientContextPool!!.key, socket)
            }
            return socket
        } else {
            return createSocket(routes, connectTimeout)
        }
    }

    private fun getSocketWithCache(key: Context.Key<*>, routes: List<Route>): StateSocket? {
        for (route in routes) {
            val socket = get(key, route)
            if (socket != null) {
                return socket
            }
        }
        return null
    }

    private fun createSocket(routes: List<Route>, connectTimeout: Int): StateSocket? {
        for (route in routes) {
            try {
                val socketFactory = SocketFactorySelector.select()
                return socketFactory.createSocket(route, connectTimeout)
            } catch (e: Exception) {
                // continue
            }
        }
        return null
    }
}