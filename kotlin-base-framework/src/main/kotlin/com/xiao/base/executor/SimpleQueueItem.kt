package com.xiao.base.executor

import com.xiao.base.logging.Logging

/**
 *
 * @author lix wang
 */
class SimpleQueueItem(
    name: String,
    runnable: Runnable
) : AbstractQueueItem(name, runnable) {
    private val maxRetryTimes = 3
    private var retryTimes = 0

    override fun run() {
        val startTime = System.currentTimeMillis()
        try {
            runnable.run()
        } catch (e: Exception) {
            log.error("Task-$name execute failed. ${e.message}", e)
            retry()
        }
        log.info("Task-$name succeed, retried $retryTimes times, consume ${System.currentTimeMillis() - startTime} ms.")
    }

    private fun retry() {
        val startTime = System.currentTimeMillis()
        for (i in 1..maxRetryTimes) {
            retryTimes = i
            try {
                runnable.run()
                break
            } catch (e: Exception) {
                log.error("Task-$name retry-$retryTimes failed. ${e.message}", e)
            }
        }
        log.error("Task-$name failed, retried $retryTimes times, consume ${System.currentTimeMillis() - startTime} ms.")
        throw RuntimeException("Task-$name failed.")
    }

    companion object : Logging()
}