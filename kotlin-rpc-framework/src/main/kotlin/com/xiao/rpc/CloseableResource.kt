package com.xiao.rpc

/**
 *
 * @author lix wang
 */
interface CloseableResource {
    fun tryClose(keepAliveMills: Int): Boolean

    fun tryUse(): Boolean

    fun unUse(): Boolean
}