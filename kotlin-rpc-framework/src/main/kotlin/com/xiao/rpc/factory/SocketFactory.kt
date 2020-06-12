package com.xiao.rpc.factory

import com.xiao.base.context.BeanRegistryAware
import com.xiao.rpc.Route
import com.xiao.rpc.StateSocket

/**
 *
 * @author lix wang
 */
interface SocketFactory {
    fun createSocket(route: Route): StateSocket
}

object DefaultSocketFactory : SocketFactory {
    override fun createSocket(route: Route): StateSocket {
        return StateSocket(route)
    }
}

object SocketFactorySelector : BeanRegistryAware {
    fun select(): SocketFactory {
        return getByType(SocketFactory::class.java) ?: DefaultSocketFactory
    }
}