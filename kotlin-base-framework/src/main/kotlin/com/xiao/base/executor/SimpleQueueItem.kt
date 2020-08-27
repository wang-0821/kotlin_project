package com.xiao.base.executor

/**
 *
 * @author lix wang
 */
class SimpleQueueItem<T>(
    name: String,
    runnable: Runnable,
    private val future: WrappedFuture<T>
) : AbstractQueueItem(name, runnable) {
    private val maxRetryTimes = 3

    override fun run() {
        checkTimeTrace(future)
        future.timeTrace!!.startTime = System.currentTimeMillis()
        try {
            runnable.run()
        } catch (e: Exception) {
            retry()
        } finally {
            future.timeTrace!!.endTime = System.currentTimeMillis()
        }
    }

    private fun retry() {
        val retryTimeTraces = mutableListOf<ExecuteTimeTrace>()
        var ex: Exception? = null
        for (i in 1..maxRetryTimes) {
            val timeTrace = ExecuteTimeTrace()
            timeTrace.startTime = System.currentTimeMillis()
            try {
                runnable.run()
                future.retryTimeTraces = retryTimeTraces
                future.timeTrace!!.endTime = System.currentTimeMillis()
                break
            } catch (e: Exception) {
                ex = e
            } finally {
                timeTrace.endTime = System.currentTimeMillis()
                retryTimeTraces.add(timeTrace)
            }
        }
        throw RuntimeException("Task $name execute failed. ${ex?.message.orEmpty()}")
    }

    private fun checkTimeTrace(future: WrappedFuture<T>) {
        if (future.timeTrace == null) {
            future.timeTrace = ExecuteTimeTrace()
        }
    }
}