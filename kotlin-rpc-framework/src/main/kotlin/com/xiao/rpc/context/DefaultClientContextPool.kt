package com.xiao.rpc.context

import com.xiao.base.context.Context
import com.xiao.rpc.annotation.ClientCachePool

/**
 *
 * @author lix wang
 */
@ClientCachePool
class DefaultClientContextPool : ClientContextPool(Key) {
    companion object Key : Context.Key<DefaultClientContextPool>
}