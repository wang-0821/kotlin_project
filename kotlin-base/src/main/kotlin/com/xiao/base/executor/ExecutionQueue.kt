package com.xiao.base.executor

import com.xiao.base.logging.Logging
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
        return submitTask(taskName, task)
    }

    fun <T : Any?> submit(task: () -> T): CompletableFuture<T> {
        return submitTask(null, task)
    }

    /**
     * If task is been canceled while mayInterruptIfRunning is false,
     * [taskCount] will decrease but task is still running.
     */
    override fun taskCount(): Int {
        return taskCount
    }

    override fun taskCapacity(): Int {
        return taskMaxCount
    }

    private fun <T : Any?> submitTask(taskName: String?, task: () -> T): CompletableFuture<T> {
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