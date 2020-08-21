package com.xiao.rpc.factory

import com.xiao.rpc.Route
import com.xiao.rpc.StateSocket

/**
 *
 * @author lix wang
 */
interface SocketFactory {
    fun createSocket(route: Route): StateSocket
}