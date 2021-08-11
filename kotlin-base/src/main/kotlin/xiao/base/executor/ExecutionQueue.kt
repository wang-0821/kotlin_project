package xiao.base.executor

import xiao.base.logging.Logging
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.locks.ReentrantLock

/**
 *
 * @author lix wang
 */
class ExecutionQueue(
    name: String,
    executorService: ExecutorService,
    private val taskMaxCount: Int = Int.MAX_VALUE
) : xiao.base.executor.AbstractExecutor(name, executorService) {
    private val lock = ReentrantLock()
    private val queueIsFull = lock.newCondition()
    private var taskCount = 0

    init {
        check(taskMaxCount > 0)
    }

    fun <T> submit(taskName: String, task: () -> T): CompletableFuture<T> {
        return submitTask(taskName, task)
    }

    fun <T> submit(task: () -> T): CompletableFuture<T> {
        return submitTask(null, task)
    }

    /**
     * If task is been canceled while mayInterruptIfRunning is false,
     * [taskCount] will decrease but task is still running.
     */
    fun taskCount(): Int {
        return taskCount
    }

    override fun fastShutdown(): Future<Unit> {
        lock.lock()
        try {
            shutdown()
            if (executorService is ThreadPoolExecutor) {
                // remove all tasks
                while (executorService.queue.poll() != null) {
                    taskCount--
                }
            }
            signalNotFull()

            return CallableFuture {
                while (isShutdown()) {
                    break
                }
            }
        } finally {
            lock.unlock()
        }
    }

    private fun <T> submitTask(taskName: String?, task: () -> T): CompletableFuture<T> {
        lock.lock()
        val realTaskName = taskName ?: ""
        try {
            if (taskCount >= taskMaxCount) {
                queueIsFull.await()
            }

            val completableFuture = execute {
                val startTime = System.currentTimeMillis()
                val result = task()
                if (log.isDebugEnabled) {
                    log.info("Execute async task $realTaskName, consume ${System.currentTimeMillis() - startTime} ms.")
                }
                return@execute result
            }.apply {
                whenComplete { _, _ ->
                    consumeTask()
                }
            }

            taskCount++
            return completableFuture
        } catch (e: Exception) {
            throw IllegalStateException(
                "Submit task $realTaskName to $name ExecutionQueue failed. ${e.message}"
            )
        } finally {
            lock.unlock()
        }
    }

    private fun signalNotFull() {
        if (taskCount < taskMaxCount) {
            queueIsFull.signalAll()
        }
    }

    private fun consumeTask() {
        lock.lock()
        try {
            taskCount--
            if (taskCount < taskMaxCount) {
                queueIsFull.signalAll()
            }
        } finally {
            lock.unlock()
        }
    }

    companion object : Logging()
}