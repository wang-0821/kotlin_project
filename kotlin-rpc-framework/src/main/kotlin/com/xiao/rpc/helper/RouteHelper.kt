package com.xiao.rpc.helper

import com.xiao.rpc.Address
import com.xiao.rpc.Client
import com.xiao.rpc.Route
import com.xiao.rpc.context.RouteContextAware
import org.slf4j.LoggerFactory
import java.net.InetAddress
import java.net.InetSocketAddress

/**
 *
 * @author lix wang
 */
object RouteHelper : RouteContextAware {
    private val log = LoggerFactory.getLogger(RouteHelper::class.java)

    fun findRoutes(client: Client, address: Address): List<Route> {
        var routes: List<Route>?
        return if (client.clientContextPool != null) {
            routes = getRoute(client.clientContextPool!!.key, address)
            if (routes.isNullOrEmpty()) {
                routes = createRoutes(address)
                if (routes.isNotEmpty()) {
                    addRoutes(client.clientContextPool!!.key, address, routes)
                }
            }
            routes
        } else {
            routes = createRoutes(address)
            routes
        }
    }

    private fun createRoutes(address: Address): List<Route> {
        return try {
            InetAddress.getAllByName(address.host).asSequence()
                .map { InetSocketAddress(it, address.port) }
                .map { Route(address, it) }.toList()
        } catch (e: Exception) {
            log.error("Create routes for $address failed. ${e.message}", e)
            listOf()
        }
    }
}