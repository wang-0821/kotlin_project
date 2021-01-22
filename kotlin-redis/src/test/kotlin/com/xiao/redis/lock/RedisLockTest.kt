package com.xiao.redis.lock

import com.xiao.redis.client.RedisHelper
import com.xiao.redis.client.service.RedisService
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.time.Duration

/**
 *
 * @author lix wang
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RedisLockTest {
    lateinit var redisService: RedisService

    @BeforeAll
    fun setup() {
        redisService = RedisHelper.getRedisService(REDIS_URL)
    }

    @Test
    fun `test try redis lock`() {
        val redisLock = SharedRedisLock(KEY, "lockValue", redisService)
        try {
            redisLock.tryLock(Duration.ofSeconds(30))
            Assertions.assertTrue(redisLock.isLocked)
        } finally {
            redisLock.unlock()
        }
    }

    @Test
    fun `test try redis lock failed`() {
        val redisLock1 = SharedRedisLock(KEY, "lockValue1", redisService)
        val redisLock2 = SharedRedisLock(KEY, "lockValue2", redisService)

        try {
            val duration = Duration.ofSeconds(30)
            redisLock1.tryLock(duration)
            redisLock2.tryLock(duration)

            Assertions.assertTrue(redisLock1.isLocked)
            Assertions.assertFalse(redisLock2.isLocked)
        } finally {
            redisLock1.unlock()
            redisLock2.unlock()
        }
    }

    @Test
    fun `test try redis lock in turn`() {
        val redisLock1 = SharedRedisLock(KEY, "lockValue1", redisService)
        val redisLock2 = SharedRedisLock(KEY, "lockValue2", redisService)
        val duration = Duration.ofSeconds(30)

        try {
            redisLock1.tryLock(duration)
            Assertions.assertTrue(redisLock1.isLocked)
            Assertions.assertFalse(redisLock2.isLocked)

            redisLock1.unlock()
            redisLock2.tryLock(duration)
            Assertions.assertFalse(redisLock1.isLocked)
            Assertions.assertTrue(redisLock2.isLocked)
        } finally {
            redisLock1.unlock()
            redisLock2.unlock()
        }
    }

    @Test
    fun `test try redis lock with retry`() {
        val redisLock1 = SharedRedisLock(KEY, "lockValue1", redisService)
        val redisLock2 = SharedRedisLock(KEY, "lockValue2", redisService)

        try {
            redisLock1.tryLock(Duration.ofSeconds(2))
            Assertions.assertTrue(redisLock1.isLocked)
            Assertions.assertFalse(redisLock2.isLocked)

            redisLock2.tryLockWithRetry(Duration.ofSeconds(2), 2, Duration.ofSeconds(2))
            Assertions.assertTrue(redisLock2.isLocked)
            Assertions.assertFalse(redisLock1.isLocked)
        } finally {
            redisLock1.unlock()
            redisLock2.unlock()
        }
    }

    @Test
    fun `test try redis lock with retry failed`() {
        val redisLock1 = SharedRedisLock(KEY, "lockValue1", redisService)
        val redisLock2 = SharedRedisLock(KEY, "lockValue2", redisService)

        try {
            redisLock1.tryLock(Duration.ofSeconds(10))
            Assertions.assertTrue(redisLock1.isLocked)
            Assertions.assertFalse(redisLock2.isLocked)

            redisLock2.tryLockWithRetry(Duration.ofSeconds(2), 2, Duration.ofSeconds(2))
            Assertions.assertTrue(redisLock1.isLocked)
            Assertions.assertFalse(redisLock2.isLocked)
        } finally {
            redisLock1.unlock()
            redisLock2.unlock()
        }
    }

    @Test
    fun `test try redis lock with retry use coroutine`() {
        val redisLock1 = SharedRedisLock(KEY, "lockValue1", redisService)
        val redisLock2 = SharedRedisLock(KEY, "lockValue2", redisService)

        try {
            redisLock1.tryLock(Duration.ofSeconds(3))
            Assertions.assertTrue(redisLock1.isLocked)
            Assertions.assertFalse(redisLock2.isLocked)

            runBlocking {
                redisLock2.tryLockWithRetrySuspend(Duration.ofSeconds(2), 2, Duration.ofSeconds(2))
            }
            Assertions.assertTrue(redisLock2.isLocked)
            Assertions.assertFalse(redisLock1.isLocked)
        } finally {
            redisLock1.unlock()
            redisLock2.unlock()
        }
    }

    companion object {
        private const val REDIS_URL = "redis://localhost:6379"
        private const val KEY = "lockName"
    }
}