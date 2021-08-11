package xiao.http.factory

import xiao.http.Protocol
import xiao.http.Route
import xiao.http.io.Connection
import java.net.Socket

/**
 *
 * @author lix wang
 */
@FunctionalInterface
interface ConnectionFactory {
    fun create(socket: Socket, route: Route, protocol: Protocol = Protocol.HTTP_1_1): Connection
}