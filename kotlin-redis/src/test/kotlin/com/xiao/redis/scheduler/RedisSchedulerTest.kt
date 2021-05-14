package com.xiao.redis.scheduler

import com.xiao.base.executor.DefaultExecutorServiceFactory
import com.xiao.redis.client.RedisHelper
import com.xiao.redis.lock.RedisLock
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Duration
import java.util.concurrent.atomic.AtomicInteger

/**
 *
 * @author lix wang
 */
class RedisSchedulerTest {
    private lateinit var redisLockScheduler: RedisLockScheduler

    @BeforeEach
    fun setup() {
        val scheduledExecutorService = DefaultExecutorServiceFactory.newScheduledExecutorService(3)
        val redisService = RedisHelper.getTestingRedisService()
        val redisLock = RedisLock("TestScheduler", "TestSchedulerVal", redisService)
        redisLockScheduler = RedisLockScheduler("TestRedisLockScheduler", redisLock, scheduledExecutorService)
    }

    @Test
    fun `test redis scheduler schedule task`() {
        val value = AtomicInteger(0)
        val future1 = redisLockScheduler.schedule(Duration.ZERO) {
            Thread.sleep(600)
            value.set(2)
        }

        val future2 = redisLockScheduler.schedule(Duration.ZERO) {
            Thread.sleep(300)
            value.set(1)
        }

        future1.get()
        future2.get()

        Assertions.assertEquals(value.get(), 1)
    }

    @Test
    fun `test redis scheduler scheduleAtFixedRate task`() {
        val value = AtomicInteger(0)
        val future1 = redisLockScheduler.schedule(Duration.ZERO) {
            Thread.sleep(100)
            value.set(1)
        }

        val future2 = redisLockScheduler.scheduleAtFixedRate(Duration.ZERO, Duration.ofMillis(150)) {
            value.getAndIncrement()
        }

        future1.get()
        Assertions.assertEquals(value.get(), 1)
        Thread.sleep(100)
        future2.cancel(false)
        future2.get()
        Assertions.assertEquals(value.get(), 2)
    }

    @Test
    fun `test redis scheduler scheduleWithFixedDelay task`() {
        val value = AtomicInteger(0)
        val future1 = redisLockScheduler.schedule(Duration.ZERO) {
            Thread.sleep(100)
            value.set(1)
        }

        val future2 = redisLockScheduler.scheduleWithFixedDelay(Duration.ZERO, Duration.ofMillis(60)) {
            value.getAndIncrement()
            Thread.sleep(100)
        }

        future1.get()
        Assertions.assertEquals(value.get(), 1)
        Thread.sleep(100)
        future2.cancel(false)
        future2.get()
        Assertions.assertEquals(value.get(), 2)
    }
}