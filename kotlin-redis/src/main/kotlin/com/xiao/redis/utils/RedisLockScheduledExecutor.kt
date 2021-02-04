package com.xiao.redis.utils

import com.xiao.base.executor.BaseScheduledExecutor
import java.time.Duration
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.locks.ReentrantLock

/**
 *
 * @author lix wang
 */
abstract class RedisLockScheduledExecutor : BaseScheduledExecutor {
    private val redisLock: RedisLock
    private var taskCount: Int = 0
    private var locked = false
    private var tryLockScheduleFuture: ScheduledFuture<Unit>? = null
    private val lock = ReentrantLock()
    private val isLockedCondition = lock.newCondition()

    constructor(
        name: String,
        redisLock: RedisLock,
        scheduledExecutorService: ScheduledExecutorService
    ) : super(name, scheduledExecutorService) {
        this.redisLock = redisLock
    }

    override fun <T> schedule(delay: Duration, command: () -> T): ScheduledFuture<T> {
        val redisRetryPeriod = getRedisRetryPeriod(delay)
        tryLock(delay, redisRetryPeriod)

        return super.schedule(delay) {
            if (!locked) {
                isLockedCondition.awaitNanos()
            }
        }
    }

    override fun scheduleAtFixedRate(
        initialDelay: Duration,
        period: Duration,
        command: () -> Unit
    ): ScheduledFuture<*> {

    }

    override fun scheduleWithFixedDelay(
        initialDelay: Duration,
        delay: Duration,
        command: () -> Unit
    ): ScheduledFuture<*> {

    }

    override fun taskCount(): Int {
        return taskCount
    }

    override fun taskCapacity(): Int {
        return Int.MAX_VALUE
    }

    override fun shutdown() {
        releaseLock()
        super.shutdown()
    }

    override fun shutdownNow() {
        releaseLock()
        super.shutdownNow()
    }

    override fun isShutdown(): Boolean {
        releaseLock()
        return super.isShutdown()
    }

    private fun tryLock(delay: Duration, redisRetryPeriod: Duration) {
        tryLockScheduleFuture = super.scheduleAtFixedRate(delay, redisRetryPeriod) {
            lock.lock()
            try {
                locked = redisLock.tryLock(redisRetryPeriod.multipliedBy(2))
                if (locked) {
                    isLockedCondition.signalAll()
                }
            } finally {
                lock.unlock()
            }
        }
    }

    private fun getRedisRetryPeriod(period: Duration): Duration {
        return maxOf(period.multipliedBy(2), DEFAULT_RETRY_PERIOD)
    }

    private fun releaseLock() {
        tryLockScheduleFuture?.cancel(false)
        if (locked) {
            redisLock.unlock()
        }
    }

    companion object {
        private val WAIT_TIMEOUT = Duration.ofMinutes(5).toNanos()
        private const val RETRY_TIMES = 4
        private val DEFAULT_RETRY_PERIOD = Duration.ofSeconds(1)
    }
}