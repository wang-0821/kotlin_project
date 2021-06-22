package com.xiao.rpc.factory

import com.xiao.rpc.Route
import java.net.Socket

/**
 *
 * @author lix wang
 */
@FunctionalInterface
interface SocketFactory {
    fun createSocket(route: Route, connectTimeout: Int): Socket
}