package com.xiao.redis.client.schedule

import com.xiao.base.executor.BaseScheduledExecutor
import com.xiao.base.executor.SafeScheduledFuture
import com.xiao.redis.utils.RedisLock
import java.time.Duration
import java.util.concurrent.Callable
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.locks.LockSupport
import java.util.concurrent.locks.ReentrantLock

/**
 * [RedisDistributeScheduler] 是分布式[ScheduledExecutorService]
 * 在异步任务执行过程中，会一直锁定分布式锁。
 *
 * @author lix wang
 */
class RedisDistributeScheduler : BaseScheduledExecutor {
    private var taskCount: Int = 0
    private val redisLock: RedisLock
    private val taskMaxCount: Int
    private val lockRetryDuration: Duration
    private val taskTimeout: Duration?
    private val lock = ReentrantLock()
    private val isFullCondition = lock.newCondition()
    private val lockHolderFutures = mutableMapOf<ScheduledTask<*>, SafeScheduledFuture<*>>()
    private val taskFutures = mutableMapOf<ScheduledTask<*>, SafeScheduledFuture<*>>()

    @JvmOverloads
    constructor(
        name: String,
        redisLock: RedisLock,
        scheduledExecutorService: ScheduledExecutorService,
        taskMaxCount: Int = Int.MAX_VALUE,
        redisRetryDuration: Duration = DEFAULT_LOCK_RETRY_DURATION,
        taskTimeout: Duration? = DEFAULT_TASK_TIMEOUT
    ) : super(name, scheduledExecutorService) {
        this.redisLock = redisLock
        this.lockRetryDuration = redisRetryDuration
        this.taskTimeout = taskTimeout
        this.taskMaxCount = taskMaxCount
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> schedule(delay: Duration, command: () -> T): SafeScheduledFuture<T> {
        return execSchedule {
            val task = ScheduledTask(command, taskTimeout, false)
            val future = super.schedule(delay) {
                task.call()
            }

            taskFutures[task] = future
            future.whenComplete { _, _ ->
                taskFutures.remove(task)
            }

            return@execSchedule future
        }
    }

    override fun scheduleAtFixedRate(
        initialDelay: Duration,
        period: Duration,
        command: () -> Unit
    ): SafeScheduledFuture<Unit> {
        return execSchedule {
            val task = ScheduledTask(command, taskTimeout, true)
            val future = super.scheduleAtFixedRate(initialDelay, period) {
                task.call()
            }

            taskFutures[task] = future
            future.whenComplete { _, _ ->
                taskFutures.remove(task)
            }

            return@execSchedule future
        }
    }

    override fun scheduleWithFixedDelay(
        initialDelay: Duration,
        delay: Duration,
        command: () -> Unit
    ): SafeScheduledFuture<Unit> {
        return execSchedule {
            val task = ScheduledTask(command, taskTimeout, true)
            val future = super.scheduleWithFixedDelay(initialDelay, delay) {
                task.call()
            }

            taskFutures[task] = future
            future.whenComplete { _, _ ->
                taskFutures.remove(task)
            }

            return@execSchedule future
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
        lockHolderFutures.forEach {
            it.value.cancel(false)
        }
        taskFutures.clear()
        lockHolderFutures.clear()
    }

    private fun <T : Any?> execSchedule(block: () -> SafeScheduledFuture<T>): SafeScheduledFuture<T> {
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

    private fun tryLockAndHoldLock(task: ScheduledTask<*>): SafeScheduledFuture<Unit> {
        var future: SafeScheduledFuture<Unit>? = null
        try {
            // try lock
            val lockExpireTime = lockRetryDuration.multipliedBy(2)
            task.locked = redisLock.tryLock(lockExpireTime)

            // constantly try lock
            future = super.scheduleAtFixedRate(lockRetryDuration, lockRetryDuration) {
                val taskLocked = task.locked
                task.locked = redisLock.tryLock(lockExpireTime)
                if (!taskLocked) {
                    task.thread?.let {
                        LockSupport.unpark(it)
                    }
                }
            }

            lockHolderFutures[task] = future
            return future
        } catch (e: Exception) {
            lockHolderFutures.remove(task)
            future?.cancel(false)
            throw e
        }
    }

    private fun releaseLock(task: ScheduledTask<*>, lockHolderFuture: SafeScheduledFuture<Unit>?) {
        lockHolderFuture?.cancel(false)
        lockHolderFutures.remove(task)
        if (task.locked) {
            task.locked = false
            redisLock.unlock()
        }
    }

    @Suppress("UNCHECKED_CAST")
    private inner class ScheduledTask<T : Any?> : Callable<T> {
        private val task: () -> T
        private val timeoutNanos: Long
        private val allowSkip: Boolean
        var locked = false
        var thread: Thread? = null

        constructor(
            task: () -> T,
            timeoutDuration: Duration?,
            allowSkip: Boolean
        ) {
            this.task = task
            this.timeoutNanos = timeoutDuration?.toNanos() ?: 0
            this.allowSkip = allowSkip
        }

        override fun call(): T {
            val lockHolderFuture = tryLockAndHoldLock(this)
            try {
                if (!locked) {
                    if (allowSkip) {
                        return null as T
                    } else {
                        thread = Thread.currentThread()
                        if (timeoutNanos > 0) {
                            LockSupport.parkNanos(timeoutNanos)
                        } else {
                            LockSupport.park()
                        }
                    }
                }

                return if (locked) {
                    return task()
                } else {
                    // timeout
                    null as T
                }
            } finally {
                releaseLock(this, lockHolderFuture)
            }
        }
    }

    companion object {
        private val DEFAULT_LOCK_RETRY_DURATION = Duration.ofSeconds(10)
        private val DEFAULT_TASK_TIMEOUT = Duration.ofMinutes(5)
    }
}