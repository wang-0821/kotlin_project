package com.xiao.redis.scheduler

import com.xiao.base.executor.DefaultExecutorServiceFactory
import com.xiao.redis.client.RedisHelper
import com.xiao.redis.schedule.RedisLockScheduler
import com.xiao.redis.utils.SharedRedisLock
import org.junit.jupiter.api.BeforeEach

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
        val redisLock = SharedRedisLock("TestScheduler", "TestSchedulerVal", redisService)
        redisLockScheduler = RedisLockScheduler("TestRedisLockScheduler", redisLock, scheduledExecutorService)
    }
}