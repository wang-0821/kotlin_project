package com.xiao.rpc.handler

import com.xiao.rpc.io.Response

/**
 *
 * @author lix wang
 */
interface Handler {
    fun handle(): Response

    val chain: Chain
}