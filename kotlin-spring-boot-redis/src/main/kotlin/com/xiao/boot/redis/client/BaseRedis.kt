package com.xiao.boot.redis.client

import com.xiao.redis.client.RedisHelper
import com.xiao.redis.client.service.RedisAsyncService
import com.xiao.redis.client.service.RedisClusterAsyncService
import com.xiao.redis.client.service.RedisClusterService
import com.xiao.redis.client.service.RedisService
import util.checkMethod

/**
 *
 * @author lix wang
 */
abstract class BaseRedis(
    private vararg val uris: String
) {
    open fun createRedisClusterService(): RedisClusterService {
        check(uris.size >= 6) {
            "Redis cluster need at least 6 instances."
        }
        return RedisHelper.getRedisClusterService(uris.toSet())
    }

    open fun createRedisClusterAsyncService(): RedisClusterAsyncService {
        check(uris.size >= 6) {
            "Redis cluster need at least 6 instances."
        }
        return RedisHelper.getRedisClusterAsyncService(uris.toSet())
    }

    open fun createRedisService(): RedisService {
        check(uris.size == 1) {
            "Only one redis uri allowed."
        }
        return RedisHelper.getRedisService(uris[0])
    }

    open fun createRedisAsyncService(): RedisAsyncService {
        check(uris.size == 1) {
            "Only one redis uri allowed."
        }

        return RedisHelper.getRedisAsyncService(uris[0])
    }

    companion object {
        fun redisClusterServiceFactoryName() =
            BaseRedis::class.java.checkMethod("createRedisClusterService")
        fun redisClusterAsyncServiceFactoryName() =
            BaseRedis::class.java.checkMethod("createRedisClusterAsyncService")
        fun redisServiceFactoryName() =
            BaseRedis::class.java.checkMethod("createRedisService")
        fun redisAsyncServiceFactoryName() =
            BaseRedis::class.java.checkMethod("createRedisAsyncService")
        fun redisServiceBeanName(name: String) = "${name}RedisService"
        fun redisAsyncServiceBeanName(name: String) = "${name}RedisAsyncService"
        fun redisClusterServiceBeanName(name: String) = "${name}RedisClusterService"
        fun redisClusterAsyncServiceBeanName(name: String) = "${name}RedisClusterAsyncService"
    }
}