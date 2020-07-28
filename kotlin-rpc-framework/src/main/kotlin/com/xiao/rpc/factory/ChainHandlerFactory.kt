package com.xiao.rpc.factory

import com.xiao.base.context.BeanRegistryAware
import com.xiao.rpc.handler.*

/**
 *
 * @author lix wang
 */
interface ChainHandlerFactory {
    fun create(chain: Chain): List<Handler>
}

object DefaultChainHandlerFactory : ChainHandlerFactory {
    override fun create(chain: Chain): List<Handler> {
        return listOf(RouteHandler(chain), SocketHandler(chain), ExchangeHandler(chain))
    }
}

object ChainHandlerFactorySelector : BeanRegistryAware {
    fun select(): ChainHandlerFactory {
        return getByType(ChainHandlerFactory::class.java) ?: DefaultChainHandlerFactory
    }
}