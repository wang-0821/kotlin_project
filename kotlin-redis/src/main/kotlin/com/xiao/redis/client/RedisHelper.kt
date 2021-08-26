package com.xiao.redis.client

import com.xiao.base.util.NettyUtils
import com.xiao.redis.client.proxy.RedisAsyncServiceProxy
import com.xiao.redis.client.proxy.RedisClusterAsyncServiceProxy
import com.xiao.redis.client.proxy.RedisClusterServiceProxy
import com.xiao.redis.client.proxy.RedisServiceProxy
import com.xiao.redis.client.service.RedisAsyncService
import com.xiao.redis.client.service.RedisClusterAsyncService
import com.xiao.redis.client.service.RedisClusterService
import com.xiao.redis.client.service.RedisService
import com.xiao.redis.client.testing.proxy.TestingRedisAsyncServiceProxy
import com.xiao.redis.client.testing.proxy.TestingRedisClusterAsyncServiceProxy
import com.xiao.redis.client.testing.proxy.TestingRedisClusterServiceProxy
import com.xiao.redis.client.testing.proxy.TestingRedisServiceProxy
import io.lettuce.core.RedisClient
import io.lettuce.core.RedisURI
import io.lettuce.core.cluster.RedisClusterClient
import io.lettuce.core.resource.DefaultClientResources
import io.netty.channel.EventLoopGroup
import java.lang.reflect.Proxy
import java.time.Duration

/**
 *
 * @author lix wang
 */
object RedisHelper {
    @JvmStatic
    fun getRedisClusterService(urls: Set<String>): RedisClusterService {
        val redisURIs = urls.map { RedisURI.create(it) }
        return Proxy.newProxyInstance(
            RedisClusterService::class.java.classLoader,
            arrayOf(RedisClusterService::class.java),
            RedisClusterServiceProxy(RedisClusterClient.create(clientResources, redisURIs))
        ) as RedisClusterService
    }

    @JvmStatic
    fun getRedisClusterAsyncService(urls: Set<String>): RedisClusterAsyncService {
        val redisURIs = urls.map { RedisURI.create(it) }
        return Proxy.newProxyInstance(
            RedisClusterAsyncService::class.java.classLoader,
            arrayOf(RedisClusterAsyncService::class.java),
            RedisClusterAsyncServiceProxy(RedisClusterClient.create(clientResources, redisURIs))
        ) as RedisClusterAsyncService
    }

    @JvmStatic
    fun getRedisService(url: String): RedisService {
        return Proxy.newProxyInstance(
            RedisService::class.java.classLoader,
            arrayOf(RedisService::class.java),
            RedisServiceProxy(RedisClient.create(clientResources, RedisURI.create(url)))
        ) as RedisService
    }

    @JvmStatic
    fun getRedisAsyncService(url: String): RedisAsyncService {
        return Proxy.newProxyInstance(
            RedisAsyncService::class.java.classLoader,
            arrayOf(RedisAsyncService::class.java),
            RedisAsyncServiceProxy(RedisClient.create(clientResources, RedisURI.create(url)))
        ) as RedisAsyncService
    }

    @JvmStatic
    fun getTestingRedisService(): RedisService {
        return Proxy.newProxyInstance(
            RedisService::class.java.classLoader,
            arrayOf(RedisService::class.java),
            TestingRedisServiceProxy()
        ) as RedisService
    }

    @JvmStatic
    fun getTestingRedisAsyncService(): RedisAsyncService {
        return Proxy.newProxyInstance(
            RedisAsyncService::class.java.classLoader,
            arrayOf(RedisAsyncService::class.java),
            TestingRedisAsyncServiceProxy()
        ) as RedisAsyncService
    }

    @JvmStatic
    fun getTestingRedisClusterService(): RedisClusterService {
        return Proxy.newProxyInstance(
            RedisClusterService::class.java.classLoader,
            arrayOf(RedisClusterService::class.java),
            TestingRedisClusterServiceProxy()
        ) as RedisClusterService
    }

    @JvmStatic
    fun getTestingRedisClusterAsyncService(): RedisClusterAsyncService {
        return Proxy.newProxyInstance(
            RedisClusterAsyncService::class.java.classLoader,
            arrayOf(RedisClusterAsyncService::class.java),
            TestingRedisClusterAsyncServiceProxy()
        ) as RedisClusterAsyncService
    }

    val REDIS_TIMEOUT: Duration = Duration.ofSeconds(5)
    private val ioThreads = Runtime.getRuntime().availableProcessors()
    private val ioEventLoopGroup: EventLoopGroup = NettyUtils.getIoEventLoopGroup(ioThreads)
    // use shared io eventLoopGroup
    private val clientResources = DefaultClientResources
        .builder()
        .eventExecutorGroup(ioEventLoopGroup)
        .build()
}