package com.xiao.redis.client.proxy

import io.lettuce.core.RedisClient
import io.lettuce.core.api.StatefulRedisConnection

/**
 *
 * @author lix wang
 */
abstract class BaseRedisProxy(
    private val redisClient: RedisClient
) : AbstractRedisProxy<StatefulRedisConnection<String, String>>(redisClient) {
    override fun connect(): StatefulRedisConnection<String, String> {
        return redisClient.connect()
    }
}