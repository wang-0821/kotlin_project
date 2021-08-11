package xiao.redis.scheduler

import xiao.base.scheduler.AbstractScheduler
import xiao.redis.lock.RedisLock
import java.time.Duration
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

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
    redisRetryDuration: Duration = Duration.ofMinutes(5)
) : AbstractScheduler(name, scheduledExecutorService) {
    private val lockDuration: Duration = redisRetryDuration

    /**
     * After delay duration, [command] will allow to execute.
     * If [command] try xiao.base.lock failed before execution, [command] will be thrown away.
     */
    override fun schedule(delay: Duration, command: () -> Unit): ScheduledFuture<Unit> {
        val runnable = Runnable {
            use(command)
        }
        return super.schedule(delay) {
            runnable.run()
        }
    }

    /**
     * After delay duration，[command] will allow to execute.
     * If [command] try xiao.base.lock failed before execution, [command] will retry till succeed.
     */
    fun execute(
        name: String,
        delay: Duration,
        timeout: Duration,
        command: () -> Unit
    ): CompletableFuture<Unit> {
        check(timeout > Duration.ZERO)
        val future = CompletableFuture<Unit>()
        val task = RunnableTask(name, command, timeout, future)
        doExecute(task, delay)
        return future
    }

    /**
     * [command] task will try xiao.base.lock with [scheduleAtFixedRate], if not locked, will skip task without waiting xiao.base.lock.
     */
    override fun scheduleAtFixedRate(
        initialDelay: Duration,
        period: Duration,
        command: () -> Unit
    ): ScheduledFuture<Unit> {
        val runnable = Runnable {
            use(command)
        }
        return super.scheduleAtFixedRate(initialDelay, period) {
            runnable.run()
        }
    }

    /**
     * [command] task will skip task without waiting xiao.base.lock, if task can not acquire xiao.base.lock.
     */
    override fun scheduleWithFixedDelay(
        initialDelay: Duration,
        delay: Duration,
        command: () -> Unit
    ): ScheduledFuture<Unit> {
        val runnable = Runnable {
            use(command)
        }
        return super.scheduleWithFixedDelay(initialDelay, delay) {
            runnable.run()
        }
    }

    private fun doExecute(runnableTask: RunnableTask<*>, delay: Duration) {
        scheduledExecutorService.schedule(runnableTask, delay.toMillis(), TimeUnit.MILLISECONDS)
    }

    private fun use(task: () -> Unit) {
        if (tryLock()) {
            try {
                task()
            } finally {
                redisLock.unlock()
            }
        }
    }

    private fun tryLock(): Boolean {
        return redisLock.tryLock(lockDuration.multipliedBy(2))
    }

    private inner class RunnableTask<T>(
        private val name: String,
        private val command: () -> T,
        retryTime: Duration? = null,
        private val future: CompletableFuture<T>
    ) : Runnable {
        private var executed: Boolean = false
        private val deadline = retryTime?.let { System.currentTimeMillis() + it.toMillis() } ?: -1

        override fun run() {
            val toExecute = !executed || System.currentTimeMillis() <= deadline
            if (toExecute) {
                if (tryLock()) {
                    try {
                        command()
                        future.complete(null)
                    } catch (throwable: Throwable) {
                        future.completeExceptionally(throwable)
                    } finally {
                        redisLock.unlock()
                    }
                } else {
                    doExecute(this, Duration.ZERO)
                }
                executed = true
            } else {
                if (!future.isDone) {
                    future.completeExceptionally(TimeoutException("Task $name timeout cancelled."))
                }
            }
        }
    }
}