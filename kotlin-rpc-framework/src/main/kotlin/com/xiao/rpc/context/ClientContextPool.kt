package com.xiao.rpc.context

import com.xiao.base.context.AbstractContext
import com.xiao.base.context.Context
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

/**
 * 不同的[ClientContextPool]的key应该使用不同的值，通过这种方式能够实现[Client]缓存区的隔离。
 *
 * @author lix wang
 */
abstract class ClientContextPool : AbstractContext {
    private val maxConnectionSize: Int
    private val keepAliveTime: Long
    private val timeUnit: TimeUnit

    constructor(
        key: Context.Key<*>,
        maxConnectionSize: Int,
        keepAliveTime: Long,
        timeUnit: TimeUnit) : super(key) {
        this.maxConnectionSize = if (maxConnectionSize > 0) {
            maxConnectionSize
        } else {
            Int.MAX_VALUE
        }
        check(keepAliveTime > 0) {
            "ClientContextPool keepAliveTime must greater than 0."
        }
        this.keepAliveTime = keepAliveTime
        this.timeUnit = timeUnit
    }

    private val clientContextContainer = ConcurrentHashMap<Context.Key<*>, Context>()

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