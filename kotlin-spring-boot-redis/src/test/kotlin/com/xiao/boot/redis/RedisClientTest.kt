package com.xiao.boot.redis

import com.xiao.boot.base.testing.KtSpringTestBase
import com.xiao.boot.redis.config.DemoRedis
import com.xiao.redis.client.service.RedisAsyncService
import com.xiao.redis.client.service.RedisService
import com.xiao.redis.client.suspend
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest

/**
 *
 * @author lix wang
 */
@Disabled
@SpringBootTest(classes = [SpringRedisAutoConfiguration::class])
class RedisClientTest : KtSpringTestBase() {
    @Autowired
    @Qualifier(DemoRedis.SERVICE_NAME)
    lateinit var redisService: RedisService

    @Autowired
    @Qualifier(DemoRedis.ASYNC_SERVICE_NAME)
    lateinit var redisAsyncService: RedisAsyncService

    @Test
    fun `test redis service`() {
        redisService.del(KEY)
        redisService.set(KEY, VALUE)
        Assertions.assertEquals(redisService.get(KEY), VALUE)
    }

    @Test
    fun `test redis async service`() {
        redisAsyncService.del(KEY).get()
        redisAsyncService.set(KEY, VALUE).get()
        Assertions.assertEquals(redisAsyncService.get(KEY).get(), VALUE)
    }

    @Test
    fun `test redis coroutine async service`() {
        runBlocking {
            redisAsyncService.del(KEY).suspend().awaitNanos()
            redisAsyncService.set(KEY, VALUE).suspend().awaitNanos()
            Assertions.assertEquals(redisAsyncService.get(KEY).suspend().awaitNanos(), VALUE)
        }
    }

    companion object {
        const val KEY = "hello"
        const val VALUE = "world"
    }
}