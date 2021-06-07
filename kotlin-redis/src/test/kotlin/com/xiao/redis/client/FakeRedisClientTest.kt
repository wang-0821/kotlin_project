package com.xiao.redis.client

import com.xiao.base.testing.KtTestBase
import com.xiao.base.util.ThreadUtils
import com.xiao.redis.client.service.RedisAsyncService
import com.xiao.redis.client.service.RedisService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 *
 * @author lix wang
 */
class FakeRedisClientTest : KtTestBase() {
    private lateinit var redisService: RedisService
    private lateinit var redisAsyncService: RedisAsyncService

    @BeforeEach
    fun setup() {
        redisService = RedisHelper.getTestingRedisService()
        redisAsyncService = RedisHelper.getTestingRedisAsyncService()
    }

    @Test
    fun `test redisService`() {
        Assertions.assertNull(redisService.get(KEY))
        redisService.set(KEY, VALUE)
        Assertions.assertEquals(redisService.get(KEY), VALUE)
        redisService.del(KEY)
        Assertions.assertNull(redisService.get(KEY))
    }

    @Test
    fun `test redisService expire`() {
        Assertions.assertNull(redisService.get(KEY))
        redisService.set(KEY, VALUE)
        Assertions.assertEquals(redisService.get(KEY), VALUE)
        redisService.expire(KEY, 0)
        ThreadUtils.safeSleep(10)
        Assertions.assertNull(redisService.get(KEY))
    }

    @Test
    fun `test redisAsyncService`() {
        Assertions.assertNull(redisAsyncService.get(KEY).get())
        redisAsyncService.set(KEY, VALUE).get()
        Assertions.assertEquals(redisAsyncService.get(KEY).get(), VALUE)
        redisAsyncService.del(KEY).get()
        Assertions.assertNull(redisAsyncService.get(KEY).get())
    }

    @Test
    fun `test redisAsyncService expire`() {
        Assertions.assertNull(redisAsyncService.get(KEY).get())
        redisAsyncService.set(KEY, VALUE).get()
        Assertions.assertEquals(redisAsyncService.get(KEY).get(), VALUE)
        redisAsyncService.expire(KEY, 0).get()
        ThreadUtils.safeSleep(10)
        Assertions.assertNull(redisAsyncService.get(KEY).get())
    }

    companion object {
        private const val KEY = "Hello"
        private const val VALUE = "world!"
    }
}