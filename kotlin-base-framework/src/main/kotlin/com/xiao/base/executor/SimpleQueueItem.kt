package com.xiao.base.executor

import org.slf4j.LoggerFactory

/**
 *
 * @author lix wang
 */
class SimpleQueueItem<T>(
    name: String,
    runnable: Runnable,
    private val future: WrappedFuture<T>
) : AbstractQueueItem(name, runnable) {
    private val log = LoggerFactory.getLogger(SimpleQueueItem::class.java)
    private val maxRetryTimes = 3

    override fun run() {
        checkTimeTrace(future)
        future.timeTrace!!.startTime = System.currentTimeMillis()
        try {
            runnable.run()
        } catch (e: Exception) {
            log.error("Execute async task $name failed. ${e.message}", e)
            retry()
        } finally {
            future.timeTrace!!.endTime = System.currentTimeMillis()
        }
    }

    private fun retry() {
        val retryTimeTraces = mutableListOf<ExecuteTimeTrace>()
        for (i in 1..maxRetryTimes) {
            val timeTrace = ExecuteTimeTrace()
            timeTrace.startTime = System.currentTimeMillis()
            try {
                runnable.run()
                future.retryTimeTraces = retryTimeTraces
                future.timeTrace!!.endTime = System.currentTimeMillis()
                break
            } catch (e: Exception) {
                log.error("Retry-$i async task $name failed. ${e.message}", e)
            } finally {
                timeTrace.endTime = System.currentTimeMillis()
                retryTimeTraces.add(timeTrace)
            }
        }
        throw RuntimeException("Retry async task $name failed.")
    }

    private fun checkTimeTrace(future: WrappedFuture<T>) {
        if (future.timeTrace == null) {
            future.timeTrace = ExecuteTimeTrace()
        }
    }
}