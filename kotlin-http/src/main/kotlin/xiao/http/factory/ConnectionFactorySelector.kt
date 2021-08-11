package xiao.http.factory

import xiao.http.Protocol
import xiao.http.Route
import xiao.http.io.Connection
import xiao.http.io.HttpConnection
import java.net.Socket

/**
 *
 * @author lix wang
 */
object ConnectionFactorySelector : AbstractSelector<ConnectionFactory>() {
    override fun selectDefault(): ConnectionFactory {
        return object : ConnectionFactory {
            override fun create(socket: Socket, route: Route, protocol: Protocol): Connection {
                val result = HttpConnection(route, socket, null)
                result.connect()
                return result
            }
        }
    }
}