package com.xiao.rpc.helper

/**
 *
 * @author lix wang
 */
object RpcHelper {
    private val rpcContextThreadLocal = ThreadLocal<RpcContext>()

    fun deleteRpc() {
        rpcContextThreadLocal.remove()
    }

    fun <T : Any> fetch(rpcContentKey: RpcContextKey<*>, valueGenerator: () -> T): T {
        val context = getOrCreateRpcContext()
        @Suppress("UNCHECKED_CAST")
        var value = context.get(rpcContentKey) as T?
        if (value == null) {
            value = valueGenerator()
            context.set(rpcContentKey, value)
        }
        return value
    }

    private fun getOrCreateRpcContext(): RpcContext {
        var context = rpcContextThreadLocal.get()
        if (context == null) {
            context = RpcContext()
        }
        rpcContextThreadLocal.set(context)
        return context
    }
}

class RpcContext {
    private val map = mutableMapOf<RpcContextKey<*>, Any>()

    fun get(key: RpcContextKey<*>): Any? {
        return map[key]
    }

    fun set(key: RpcContextKey<*>, value: Any) {
        map[key] = value
    }
}

interface RpcContextKey<E : Any>