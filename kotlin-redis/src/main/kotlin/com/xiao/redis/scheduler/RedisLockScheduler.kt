package com.xiao.redis.scheduler

import com.xiao.base.scheduler.AbstractScheduler
import com.xiao.base.scheduler.SafeScheduledFuture
import com.xiao.redis.lock.RedisLock
import java.time.Duration
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.locks.ReentrantLock

/**
 * [RedisLockScheduler] 是分布式[ScheduledExecutorService]
 * 在异步任务执行过程中，会一直锁定分布式锁。
 *
 * @author lix wang
 */
class RedisLockScheduler(
    name: String,
    private val redisLock: RedisLock,
    scheduledExecutorService: ScheduledExecutorService,
    redisRetryDuration: Duration = Duration.ofMinutes(5),
    private val taskMaxCount: Int = Int.MAX_VALUE
) : AbstractScheduler(name, scheduledExecutorService) {
    private var taskCount: Int = 0
    private val lockDuration: Duration = redisRetryDuration
    private val lock = ReentrantLock()
    private val isFullCondition = lock.newCondition()
    private val taskFutures = mutableMapOf<Runnable, SafeScheduledFuture<*>>()

    /**
     * [command] will skip while tryLock failed.
     */
    override fun schedule(delay: Duration, command: () -> Unit): SafeScheduledFuture<Unit> {
        return execWithLock {
            val runnable = toRunnable(command)
            val future = super.schedule(delay) {
                runnable.run()
            }

            taskFutures[runnable] = future
            future.whenComplete { _, _ ->
                taskFutures.remove(runnable)
            }

            return@execWithLock future
        }
    }

    /**
     * [command] task will try lock with [scheduleAtFixedRate], if not locked, will skip task without waiting lock.
     */
    override fun scheduleAtFixedRate(
        initialDelay: Duration,
        period: Duration,
        command: () -> Unit
    ): SafeScheduledFuture<Unit> {
        return execWithLock {
            val runnable = toRunnable(command)
            val future = super.scheduleAtFixedRate(initialDelay, period) {
                runnable.run()
            }

            taskFutures[runnable] = future
            future.whenComplete { _, _ ->
                taskFutures.remove(runnable)
            }

            return@execWithLock future
        }
    }

    /**
     * [command] task will skip task without waiting lock, if task can not acquire lock.
     */
    override fun scheduleWithFixedDelay(
        initialDelay: Duration,
        delay: Duration,
        command: () -> Unit
    ): SafeScheduledFuture<Unit> {
        return execWithLock {
            val runnable = toRunnable(command)
            val future = super.scheduleWithFixedDelay(initialDelay, delay) {
                runnable.run()
            }

            taskFutures[runnable] = future
            future.whenComplete { _, _ ->
                taskFutures.remove(runnable)
            }

            return@execWithLock future
        }
    }

    override fun taskCount(): Int {
        return taskCount
    }

    override fun taskCapacity(): Int {
        return Int.MAX_VALUE
    }

    override fun shutdown() {
        cancelAllScheduledTask()
        super.shutdown()
    }

    override fun shutdownNow() {
        cancelAllScheduledTask()
        super.shutdownNow()
    }

    private fun cancelAllScheduledTask() {
        taskFutures.forEach {
            it.value.cancel(false)
        }
        taskFutures.clear()
    }

    private fun <T : Any?> execWithLock(block: () -> SafeScheduledFuture<T>): SafeScheduledFuture<T> {
        lock.lock()
        try {
            if (taskCount >= taskMaxCount) {
                isFullCondition.await()
            }
            val future = block()

            taskCount++
            return future
        } finally {
            lock.unlock()
        }
    }

    private fun toRunnable(task: () -> Unit): Runnable {
        return Runnable {
            if (redisLock.tryLock(lockDuration.multipliedBy(2))) {
                try {
                    task()
                } finally {
                    redisLock.unlock()
                }
            }
        }
    }
}