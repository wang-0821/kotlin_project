package com.xiao.rpc.handler

import com.xiao.base.exception.KtException
import com.xiao.rpc.protocol.Response

/**
 *
 * @author lix wang
 */
@FunctionalInterface
interface Handler {
    @Throws(KtException::class)
    fun handle(): Response

    val chain: Chain
}