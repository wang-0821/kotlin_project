package com.xiao.rpc.handler

import com.xiao.base.exception.KtException
import com.xiao.rpc.io.Response

/**
 *
 * @author lix wang
 */
interface Handler {
    @Throws(KtException::class)
    fun handle(): Response

    val chain: Chain
}