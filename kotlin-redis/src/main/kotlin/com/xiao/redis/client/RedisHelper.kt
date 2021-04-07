package com.xiao.redis.client

import com.xiao.base.util.NettyUtils
import com.xiao.redis.client.proxy.RedisAsyncServiceProxy
import com.xiao.redis.client.proxy.RedisServiceProxy
import com.xiao.redis.client.service.RedisAsyncService
import com.xiao.redis.client.service.RedisService
import com.xiao.redis.client.testing.TestingRedisAsyncServiceProxy
import com.xiao.redis.client.testing.TestingRedisServiceProxy
import io.lettuce.core.RedisClient
import io.lettuce.core.RedisURI
import io.lettuce.core.resource.DefaultClientResources
import io.lettuce.core.resource.EventLoopGroupProvider
import io.netty.channel.EventLoopGroup
import io.netty.util.concurrent.DefaultPromise
import io.netty.util.concurrent.EventExecutorGroup
import io.netty.util.concurrent.Future
import io.netty.util.concurrent.GlobalEventExecutor
import java.lang.reflect.Proxy
import java.util.concurrent.TimeUnit

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

    private val ioThreads = Runtime.getRuntime().availableProcessors()
    private val ioEventLoopGroup: EventLoopGroup = NettyUtils.getIoEventLoopGroup(ioThreads)
    // use shared io eventLoopGroup
    private val clientResources = DefaultClientResources
        .builder()
        .eventExecutorGroup(ioEventLoopGroup)
        .eventLoopGroupProvider(
            object : EventLoopGroupProvider {
                @Suppress("UNCHECKED_CAST")
                override fun <T : EventLoopGroup?> allocate(type: Class<T>): T {
                    if (type.isAssignableFrom(ioEventLoopGroup::class.java)) {
                        return ioEventLoopGroup as T
                    } else {
                        throw IllegalArgumentException("Unexpected eventLoopGroup type: ${type.simpleName}.")
                    }
                }

                override fun threadPoolSize(): Int {
                    return ioThreads
                }

                override fun release(
                    eventLoopGroup: EventExecutorGroup,
                    quietPeriod: Long,
                    timeout: Long,
                    unit: TimeUnit?
                ): Future<Boolean> {
                    return DefaultPromise<Boolean>(GlobalEventExecutor.INSTANCE)
                        .apply {
                            setSuccess(true)
                        }
                }

                override fun shutdown(quietPeriod: Long, timeout: Long, timeUnit: TimeUnit?): Future<Boolean> {
                    return DefaultPromise<Boolean>(GlobalEventExecutor.INSTANCE)
                        .apply {
                            setSuccess(true)
                        }
                }
            }
        )
        .build()
}