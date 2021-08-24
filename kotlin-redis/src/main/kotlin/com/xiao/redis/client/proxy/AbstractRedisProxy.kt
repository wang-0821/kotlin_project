package com.xiao.redis.client.proxy

import com.xiao.redis.client.RedisHelper
import io.lettuce.core.AbstractRedisClient
import io.lettuce.core.api.StatefulConnection
import java.lang.reflect.InvocationHandler

/**
 *
 * @author lix wang
 */
abstract class AbstractRedisProxy<T : StatefulConnection<String, String>>(
    private val redisClient: AbstractRedisClient
) : InvocationHandler {
    @Volatile private var connection: T? = null

    fun getConnection(): T {
        return if (isConnectionValid()) {
            connection!!
        } else {
            val oldConnection = connection
            synchronized(redisClient) {
                if (isConnectionValid() && oldConnection != connection) {
                    return connection!!
                }
                redisClient.defaultTimeout = RedisHelper.REDIS_TIMEOUT
                connection = connect()
                connection!!
            }
        }
    }

    protected abstract fun connect(): T

    private fun isConnectionValid(): Boolean {
        return connection != null && connection!!.isOpen
    }
}