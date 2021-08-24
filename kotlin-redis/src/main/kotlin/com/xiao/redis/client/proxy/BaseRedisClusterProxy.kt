package com.xiao.redis.client.proxy

import io.lettuce.core.cluster.RedisClusterClient
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection

/**
 *
 * @author lix wang
 */
abstract class BaseRedisClusterProxy(
    private val redisClient: RedisClusterClient
) : AbstractRedisProxy<StatefulRedisClusterConnection<String, String>>(redisClient) {
    override fun connect(): StatefulRedisClusterConnection<String, String> {
        return redisClient.connect()
    }
}