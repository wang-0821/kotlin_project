package com.xiao.base.executor

import com.xiao.base.logging.Logging
import java.util.concurrent.Callable

/**
 *
 * @author lix wang
 */
abstract class QueueItem<T>(val name: String, private val maxRetryTimes: Int = 2) : Callable<T> {
    private var retriedTimes = 0

    override fun call(): T {
        val startTime = System.currentTimeMillis()
        val result = try {
            execute()
        } catch (e: Exception) {
            log.error("Task-$name execute failed. ${e.message}", e)
            retry()
        }
        log.info(
            "Task-$name succeed, retried $retriedTimes times, consume ${System.currentTimeMillis() - startTime} ms."
        )
        return result
    }

    protected abstract fun execute(): T

    private fun retry(): T {
        val startTime = System.currentTimeMillis()
        for (i in 1..maxRetryTimes + 1) {
            retriedTimes++
            try {
                return execute()
            } catch (e: Exception) {
                log.error("Task-$name retry-$retriedTimes failed. ${e.message}", e)
            }
        }
        log.error(
            "Task-$name failed, retried $retriedTimes times, consume ${System.currentTimeMillis() - startTime} ms."
        )
        throw RuntimeException("Task-$name failed.")
    }

    companion object : Logging()
}