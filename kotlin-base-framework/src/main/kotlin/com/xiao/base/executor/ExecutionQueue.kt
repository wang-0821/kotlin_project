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

    fun <T> submit(taskName: String, callable: Callable<T>): Future<T> {
        val name = if (taskName.isNullOrBlank()) {
            "Queue-Callable"
        } else {
            taskName
        }
        val queueItem = object : QueueItem<T>(name) {
            override fun execute(): T {
                return callable.call()
            }
        }
        return submitCallable(queueItem)
    }

    fun <T> submit(callable: Callable<T>): Future<T> {
        return submit("", callable)
    }

    fun submit(taskName: String, runnable: Runnable) {
        val name = if (taskName.isBlank()){
            "Queue-Runnable"
        } else {
            taskName
        }
        val queueItem = object : QueueItem<Void>(name) {
            override fun execute(): Void {
                return runnable.run() as Void
            }
        }
        submitRunnable(queueItem)
    }

    fun submit(runnable: Runnable) {
        submit("", runnable)
    }

    private fun <T> submitCallable(queueItem: QueueItem<T>): Future<T> {
        lock.lock()
        try {
            val future = FutureTask<T>(queueItem)
            executorService.execute(future)
            return future
        } catch (e: Exception) {
            throw IllegalStateException(
                "Submit task ${queueItem.name} to $executionQueueName ExecutionQueue failed. ${e.message}")
        } finally {
            lock.unlock()
        }
    }

    private fun submitRunnable(queueItem: QueueItem<Void>) {
        lock.lock()
        try {
            executorService.execute { queueItem.execute() }
        } catch (e: Exception) {
            throw IllegalStateException(
                "Submit task ${queueItem.name} to $executionQueueName ExecutionQueue failed. ${e.message}")
        } finally {
            lock.unlock()
        }
    }
}