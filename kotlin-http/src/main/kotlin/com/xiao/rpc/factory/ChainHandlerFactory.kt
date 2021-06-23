package com.xiao.rpc.factory

import com.xiao.rpc.handler.Chain
import com.xiao.rpc.handler.Handler

/**
 *
 * @author lix wang
 */
@FunctionalInterface
interface ChainHandlerFactory {
    fun create(chain: Chain): List<Handler>
}