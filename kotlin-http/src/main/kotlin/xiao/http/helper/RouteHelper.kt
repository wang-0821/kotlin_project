package xiao.http.helper

import xiao.base.logging.Logging
import xiao.http.Address
import xiao.http.Client
import xiao.http.Route
import xiao.http.context.RouteContextAware
import java.net.InetAddress
import java.net.InetSocketAddress

/**
 *
 * @author lix wang
 */
object RouteHelper : RouteContextAware, Logging() {
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

    private fun createRoutes(address: xiao.http.Address): List<Route> {
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