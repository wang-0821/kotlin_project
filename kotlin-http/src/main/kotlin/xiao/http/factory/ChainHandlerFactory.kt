package xiao.http.factory

import xiao.http.handler.Chain
import xiao.http.handler.Handler

/**
 *
 * @author lix wang
 */
@FunctionalInterface
interface ChainHandlerFactory {
    fun create(chain: Chain): List<Handler>
}