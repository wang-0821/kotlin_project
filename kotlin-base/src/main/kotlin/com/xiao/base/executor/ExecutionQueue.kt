package com.xiao.base.executor

import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.locks.ReentrantLock

/**
 *
 * @author lix wang
 */
class ExecutionQueue : BaseExecutor {
    private val lock = ReentrantLock()
    private val queueIsFull = lock.newCondition()
    private val taskMaxCount: Int
    private var taskCount = 0

    constructor(
        executionQueueName: String,
        executorService: ExecutorService,
        taskMaxCount: Int = Int.MAX_VALUE
    ) : super(executionQueueName, executorService) {
        check(taskMaxCount > 0)
        this.taskMaxCount = taskMaxCount
    }

    fun <T : Any?> submit(taskName: String, task: () -> T): CompletableFuture<T> {
        val queueItem = getQueueItem(taskName, task)
        return submitQueueItem(queueItem)
    }

    fun <T : Any?> submit(task: () -> T): CompletableFuture<T> {
        val queueItem = getQueueItem(null, task)
        return submitQueueItem(queueItem)
    }

    override fun taskCount(): Int {
        return taskCount
    }

    override fun taskCapacity(): Int {
        return taskMaxCount
    }

    private fun <T : Any?> getQueueItem(name: String?, task: () -> T): QueueItem<T> {
        return object : QueueItem<T>(name ?: "") {
            override fun execute(): T {
                return task()
            }
        }
    }

    private fun <T : Any?> submitQueueItem(queueItem: QueueItem<T>): CompletableFuture<T> {
        lock.lock()
        try {
            if (taskCount >= taskMaxCount) {
                queueIsFull.await()
            }

            val completableFuture = execute { queueItem.call() }
                .apply {
                    whenComplete { _, _ ->
                        consumeTask()
                    }
                }

            taskCount++
            return completableFuture
        } catch (e: Exception) {
            throw IllegalStateException(
                "Submit task ${queueItem.name} to $name ExecutionQueue failed. ${e.message}"
            )
        } finally {
            lock.unlock()
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
}