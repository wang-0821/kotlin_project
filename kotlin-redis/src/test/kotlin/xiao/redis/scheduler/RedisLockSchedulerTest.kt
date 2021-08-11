package xiao.redis.scheduler

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import xiao.base.executor.DefaultExecutorServiceFactory
import xiao.base.testing.KtTestBase
import xiao.redis.client.RedisHelper
import xiao.redis.client.service.RedisService
import xiao.redis.lock.RedisLock
import java.time.Duration
import java.util.concurrent.TimeUnit

/**
 *
 * @author lix wang
 */
class RedisLockSchedulerTest : KtTestBase() {
    lateinit var redisService: RedisService

    @BeforeAll
    fun setup() {
        redisService = RedisHelper.getTestingRedisService()
    }

    @Test
    fun `test schedule try lock succeed`() {
        val redisLock = RedisLock("redis-xiao.base.lock-1", "xiao.base.lock-value-1", redisService)
        val scheduler = RedisLockScheduler(
            "redis-xiao.base.lock-scheduler-test1",
            redisLock,
            DefaultExecutorServiceFactory.newScheduledExecutorService(1)
        )

        var value = 0
        val future = scheduler.schedule(Duration.ZERO) {
            value = 1
        }
        future.get()
        Assertions.assertEquals(value, 1)
    }

    @Test
    fun `test schedule try lock failed`() {
        val redisLock = RedisLock("redis-xiao.base.lock-2", "xiao.base.lock-value-2", redisService)
        val scheduler = RedisLockScheduler(
            "redis-xiao.base.lock-scheduler-test2",
            redisLock,
            DefaultExecutorServiceFactory.newScheduledExecutorService(1)
        )

        redisLock.tryLock(Duration.ofSeconds(30))
        Assertions.assertTrue(redisLock.isLocked())

        var value = 0
        try {
            scheduler.schedule(Duration.ZERO) {
                value = 1
            }.get()
        } finally {
            redisLock.unlock()
        }
        Assertions.assertEquals(value, 0)
    }

    @Test
    fun `test schedule execute`() {
        val redisLock = RedisLock("redis-xiao.base.lock-3", "xiao.base.lock-value-3", redisService)
        val scheduler = RedisLockScheduler(
            "redis-xiao.base.lock-scheduler-test3",
            redisLock,
            DefaultExecutorServiceFactory.newScheduledExecutorService(1)
        )

        var value = 0
        scheduler.execute("schedule-exec-test", Duration.ZERO, Duration.ofSeconds(5)) {
            value = 1
        }.get(5, TimeUnit.SECONDS)

        Assertions.assertEquals(value, 1)
    }

    @Test
    fun `test schedule execute retry succeed`() {
        val redisLock = RedisLock("redis-xiao.base.lock-4", "xiao.base.lock-value-4", redisService)
        val scheduler = RedisLockScheduler(
            "redis-xiao.base.lock-scheduler-test4",
            redisLock,
            DefaultExecutorServiceFactory.newScheduledExecutorService(1)
        )

        var value = 0
        redisLock.tryLock(Duration.ofSeconds(1))
        val future = scheduler.execute("schedule-exec-test", Duration.ZERO, Duration.ofSeconds(5)) {
            value = 1
        }
        Thread.sleep(200)
        Assertions.assertEquals(value, 0)

        redisLock.unlock()
        future.get()
        Assertions.assertEquals(value, 1)
    }
}