package com.xiao.base.executor

import com.xiao.base.executor.QueueItemHelper.getQueueItem
import java.util.concurrent.Callable
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future
import java.util.concurrent.FutureTask
import java.util.concurrent.locks.ReentrantLock

/**
 *
 * @author lix wang
 */
class ExecutionQueue {
    private val executorService: ExecutorService
    private val executionQueueName: String
    private val lock = ReentrantLock()

    constructor(executionQueueName: String, executorService: ExecutorService) {
        this.executionQueueName = executionQueueName
        this.executorService = executorService
    }

    fun <T : Any?> submit(taskName: String?, callable: Callable<T>): Future<T> {
        val name = if (taskName.isNullOrBlank()) {
            "Queue-Callable"
        } else {
            taskName
        }
        return submitCallable(getQueueItem(name, callable))
    }

    fun <T : Any?> submit(queueItem: QueueItem<T>): Future<T> {
        return submitCallable(queueItem)
    }

    fun <T : Any?> submit(callable: Callable<T>): Future<T> {
        return submit(null, callable)
    }

    fun submit(taskName: String?, runnable: Runnable) {
        val name = if (taskName.isNullOrBlank()) {
            "Queue-Runnable"
        } else {
            taskName
        }
        submitRunnable(name) { getQueueItem(name, runnable).call() }
    }

    fun submit(runnable: Runnable) {
        submit(null, runnable)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any?> async(taskName: String?, callable: Callable<T>): CompletableFuture<T> {
        val name = if (taskName.isNullOrBlank()) {
            "Queue-Async"
        } else taskName
        val result = CompletableFuture<Any?>()
        val queueItem = getQueueItem(name, callable) as QueueItem<Any?>
        submitRunnable(name, CompletableCallback({ queueItem.call() }, result, null))
        return result as CompletableFuture<T>
    }

    fun <T : Any?> async(callable: Callable<T>): CompletableFuture<T> {
        return async(null, callable)
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : Any?> submitCallable(queueItem: QueueItem<T>): Future<T> {
        lock.lock()
        try {
            val future = FutureTask(queueItem)
            executorService.execute(future)
            return future
        } catch (e: Exception) {
            throw IllegalStateException(
                "Submit task ${queueItem.name} to $executionQueueName ExecutionQueue failed. ${e.message}"
            )
        } finally {
            lock.unlock()
        }
    }

    private fun submitRunnable(name: String, runnable: Runnable) {
        lock.lock()
        try {
            executorService.execute(runnable)
        } catch (e: Exception) {
            throw IllegalStateException(
                "Submit task $name to $executionQueueName ExecutionQueue failed. ${e.message}"
            )
        } finally {
            lock.unlock()
        }
    }
}