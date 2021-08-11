package xiao.http.factory

import xiao.http.handler.Chain
import xiao.http.handler.ConnectionHandler
import xiao.http.handler.ExchangeHandler
import xiao.http.handler.Handler
import xiao.http.handler.RouteHandler

/**
 *
 * @author lix wang
 */
object ChainHandlerFactorySelector : AbstractSelector<ChainHandlerFactory>() {
    override fun selectDefault(): ChainHandlerFactory {
        return object : ChainHandlerFactory {
            override fun create(chain: Chain): List<Handler> {
                return listOf(RouteHandler(chain), ConnectionHandler(chain), ExchangeHandler(chain))
            }
        }
    }
}