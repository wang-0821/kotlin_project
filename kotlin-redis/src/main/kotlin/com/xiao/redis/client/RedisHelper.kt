package com.xiao.redis.client

import com.xiao.redis.client.proxy.RedisAsyncServiceProxy
import com.xiao.redis.client.proxy.RedisServiceProxy
import com.xiao.redis.client.service.RedisAsyncService
import com.xiao.redis.client.service.RedisService
import io.lettuce.core.RedisClient
import io.lettuce.core.RedisURI
import java.lang.reflect.Proxy

/**
 *
 * @author lix wang
 */
object RedisHelper {
    @JvmStatic
    fun getRedisService(url: String): RedisService {
        return Proxy.newProxyInstance(
            RedisService::class.java.classLoader,
            arrayOf(RedisService::class.java),
            RedisServiceProxy(RedisClient.create(RedisURI.create(url)))
        ) as RedisService
    }

    @JvmStatic
    fun getRedisAsyncService(url: String): RedisAsyncService {
        return Proxy.newProxyInstance(
            RedisAsyncService::class.java.classLoader,
            arrayOf(RedisAsyncService::class.java),
            RedisAsyncServiceProxy(RedisClient.create(RedisURI.create(url)))
        ) as RedisAsyncService
    }
}