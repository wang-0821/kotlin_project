package com.xiao.redis.client

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

/**
 *
 * @author lix wang
 */
class RedisClientTest {
    @Test
    fun `test redis sync commands`() {
        val redisSyncCommands = RedisHelper.getRedisService(REDIS_URL)
        redisSyncCommands.set(KEY, VALUE)
        Assertions.assertEquals(redisSyncCommands.get(KEY), VALUE)
    }

    @Test
    fun `test redis async commands`() {
        val redisAsyncCommands = RedisHelper.getRedisAsyncService(REDIS_URL)
        redisAsyncCommands.set(KEY, VALUE).get()
        Assertions.assertEquals(redisAsyncCommands.get(KEY).get(), VALUE)
    }

    companion object {
        private const val REDIS_URL = "redis://localhost:6379"
        private const val KEY = "Hello"
        private const val VALUE = "world!"
    }
}