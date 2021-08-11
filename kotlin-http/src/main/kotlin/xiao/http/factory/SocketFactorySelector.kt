package xiao.http.factory

import xiao.http.Route
import java.net.Socket

/**
 *
 * @author lix wang
 */
object SocketFactorySelector : AbstractSelector<SocketFactory>() {
    override fun selectDefault(): SocketFactory {
        return object : SocketFactory {
            override fun createSocket(route: Route, connectTimeout: Int): Socket {
                val socket = Socket()
                socket.keepAlive = true
                socket.reuseAddress = true
                socket.tcpNoDelay = true
                socket.connect(route.inetSocketAddress, connectTimeout)
                return socket
            }
        }
    }
}