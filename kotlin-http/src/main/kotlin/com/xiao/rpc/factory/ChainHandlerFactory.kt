package com.xiao.rpc.factory

import com.xiao.rpc.handler.Chain
import com.xiao.rpc.handler.Handler

/**
 *
 * @author lix wang
 */
interface ChainHandlerFactory {
    fun create(chain: Chain): List<Handler>
}