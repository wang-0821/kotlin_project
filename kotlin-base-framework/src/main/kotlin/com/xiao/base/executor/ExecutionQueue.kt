package com.xiao.base.executor

import java.util.concurrent.Callable
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

    fun <T> submit(taskName: String?, callable: Callable<T>): Future<T> {
        lock.lock()
        val name = taskName ?: "Queue-Item"
        try {
            val queueItem = QueueItem(name, FutureTask(callable))
            return executorService.submit(callable)
        } catch (e: Exception) {
            throw IllegalStateException(
                "Submit task $taskName to $executionQueueName ExecutionQueue failed. ${e.message}")
        } finally {
            lock.unlock()
        }
    }

    fun <T> submit(callable: Callable<T>): Future<T> {
        return submit(null, callable)
    }
}