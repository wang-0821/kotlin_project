package com.xiao.redis.client

import io.lettuce.core.RedisClient
import io.lettuce.core.RedisURI
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.api.sync.RedisCommands
import java.time.Duration

/**
 *
 * @author lix wang
 */
object RedisHelper {
    @JvmStatic
    fun getRedisService(url: String): RedisCommands<String, String> {
        return fetchConnection(url).sync()
    }

    private fun fetchConnection(url: String): StatefulRedisConnection<String, String> {
        val redisClient = RedisClient.create(RedisURI.create(url))
        redisClient.defaultTimeout = Duration.ofSeconds(REDIS_TIMEOUT_SECONDS)
        return redisClient.connect()
    }

    private const val REDIS_TIMEOUT_SECONDS = 30L
}