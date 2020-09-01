package com.xiao.rpc.context

import com.xiao.base.context.Context

/**
 *
 * @author lix wang
 */
class DefaultClientContextPool : ClientContextPool(Key) {
    companion object Key : Context.Key<DefaultClientContextPool>
    override val key: Context.Key<*>
        get() = Key
}