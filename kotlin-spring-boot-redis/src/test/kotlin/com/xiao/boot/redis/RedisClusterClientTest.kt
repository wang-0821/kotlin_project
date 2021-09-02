package com.xiao.boot.redis

import com.xiao.boot.base.testing.KtSpringTestBase
import com.xiao.boot.redis.config.DemoClusterRedis
import com.xiao.redis.client.service.RedisClusterAsyncService
import com.xiao.redis.client.service.RedisClusterService
import com.xiao.redis.client.suspend
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest

/**
 *
 * @author lix wang
 */
@SpringBootTest(classes = [SpringRedisAutoConfiguration::class])
class RedisClusterClientTest : KtSpringTestBase() {
    @Autowired
    @Qualifier(DemoClusterRedis.CLUSTER_SERVICE_NAME)
    lateinit var redisClusterService: RedisClusterService

    @Autowired
    @Qualifier(DemoClusterRedis.CLUSTER_ASYNC_SERVICE_NAME)
    lateinit var redisClusterAsyncService: RedisClusterAsyncService

    @Test
    fun `test redis cluster service`() {
        redisClusterService.del(KEY)
        redisClusterService.set(KEY, VALUE)
        Assertions.assertEquals(redisClusterService.get(KEY), VALUE)
    }

    @Test
    fun `test redis cluster async service`() {
        redisClusterAsyncService.del(KEY).get()
        redisClusterAsyncService.set(KEY, VALUE).get()
        Assertions.assertEquals(redisClusterAsyncService.get(KEY).get(), VALUE)
    }

    @Test
    fun `test redis cluster coroutine async service`() {
        runBlocking {
            redisClusterAsyncService.del(KEY).suspend().awaitNanos()
            redisClusterAsyncService.set(KEY, VALUE).suspend().awaitNanos()
            Assertions.assertEquals(redisClusterAsyncService.get(KEY).suspend().awaitNanos(), VALUE)
        }
    }

    companion object {
        const val KEY = "hello"
        const val VALUE = "world"
    }
}