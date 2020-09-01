package com.xiao.rpc.context

import com.xiao.base.context.AbstractContext
import com.xiao.base.context.Context
import com.xiao.base.context.ContextScanner
import com.xiao.rpc.Client
import com.xiao.rpc.annotation.AutoClean
import com.xiao.rpc.annotation.ClientContext
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

/**
 * 不同的[ClientContextPool]的key应该使用不同的值，通过这种方式能够实现[Client]缓存区的隔离。
 *
 * @author lix wang
 */
abstract class ClientContextPool(key: Context.Key<*>) : AbstractContext(key) {
    private val contextClientConfig = mutableMapOf<Context.Key<*>, ClientContextConfig>()
    private val defaultClientContextConfig = ClientContextConfig(16, 60, TimeUnit.SECONDS)
    private val clientContextContainer = ConcurrentHashMap<Context.Key<*>, Context>()

    fun clientConfig(key: Context.Key<*>, config: ClientContextConfig) {
        contextClientConfig[key] = config
    }

    fun clientConfig(key: Context.Key<*>): ClientContextConfig {
        return contextClientConfig[key] ?: defaultClientContextConfig
    }

    fun start() {
        ContextScanner.scanAndExecute(Client.BASE_SCAN_PACKAGE)
        val clientContextList = ContextScanner.annotatedKtResources.filter { it.isAnnotated(ClientContext::class) }
        val contextCleaners = ContextScanner.annotatedKtResources.filter { it.isAnnotated(AutoClean::class) }
        println("*******")
    }

    fun registerContext(key: Context.Key<*>, context: Context) {
        synchronized(clientContextContainer) {
            if (clientContextContainer[key] != null) {
                throw IllegalStateException("Duplicate key $key.")
            }
            clientContextContainer[key] = context
        }
    }

    fun getContext(key: Context.Key<*>): Context? {
        return clientContextContainer[key]
    }
}