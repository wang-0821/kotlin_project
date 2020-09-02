package com.xiao.rpc.context

import com.xiao.base.context.Context
import com.xiao.base.context.ContextAware

/**
 * 所有实现了[ClientContextAware]的类，都会被当作[ClientContextPool]中的一种缓存类型，
 * 进而会在初始化[Client]时，产生一个对应的缓存类型实例，注入到当前[Client]的缓存区[ClientContextPool]中。
 *
 * @author lix wang
 */
@Suppress("UNCHECKED_CAST")
interface ClientContextAware : ContextAware {
    fun getContext(contextPoolKey: Context.Key<*>, contextKey: Context.Key<*>): Context? {
        val clientContext = getClientContext<ClientContextPool>(contextPoolKey)
        return clientContext?.getContext(contextKey)
    }

    private fun <T : ClientContextPool> getClientContext(key: Context.Key<*>): T? {
        return get(key) as T?
    }
}