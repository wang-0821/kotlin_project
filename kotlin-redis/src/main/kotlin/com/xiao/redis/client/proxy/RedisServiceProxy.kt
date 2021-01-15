package com.xiao.redis.client.proxy

import io.lettuce.core.RedisClient
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method

/**
 *
 * @author lix wang
 */
class RedisServiceProxy<T>(
    private val target: T,
    private val targetClass: Class<T>,
    private val redisClient: RedisClient
) : InvocationHandler {
    override fun invoke(proxy: Any?, method: Method, args: Array<Any?>?): Any {
        TODO("Not yet implemented")
    }
}