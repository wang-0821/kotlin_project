package com.xiao.rpc.factory

import com.xiao.rpc.handler.Chain
import com.xiao.rpc.handler.ConnectionHandler
import com.xiao.rpc.handler.ExchangeHandler
import com.xiao.rpc.handler.Handler

/**
 *
 * @author lix wang
 */
object ChainHandlerFactorySelector : AbstractSelector<ChainHandlerFactory>() {
    override fun selectDefault(): ChainHandlerFactory {
        return object : ChainHandlerFactory {
            override fun create(chain: Chain): List<Handler> {
                return listOf(ConnectionHandler(chain), ExchangeHandler(chain))
            }
        }
    }
}