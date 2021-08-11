package xiao.http.factory

import xiao.http.Route
import java.net.Socket

/**
 *
 * @author lix wang
 */
@FunctionalInterface
interface SocketFactory {
    fun createSocket(route: Route, connectTimeout: Int): Socket
}