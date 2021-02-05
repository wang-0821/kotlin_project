package com.xiao.base.executor

import java.util.concurrent.ExecutorService
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 *
 * @author lix wang
 */
object DefaultExecutorServiceFactory : ExecutorServiceFactory {
    override fun newExecutorService(threadCount: Int): ExecutorService {
        return executorService(null, threadCount)
    }

    override fun newExecutorService(threadName: String, threadCount: Int): ExecutorService {
        return executorService(threadName, threadCount)
    }

    override fun newScheduledExecutorService(threadCount: Int): ScheduledExecutorService {
        return scheduledExecutorService(null, threadCount)
    }

    override fun newScheduledExecutorService(threadName: String, threadCount: Int): ScheduledExecutorService {
        return scheduledExecutorService(threadName, threadCount)
    }

    private fun executorService(threadName: String?, threadCount: Int): ExecutorService {
        return ThreadPoolExecutor(
            threadCount,
            threadCount,
            0,
            TimeUnit.SECONDS,
            LinkedBlockingDeque(),
            NamedThreadFactory(threadName)
        )
    }

    private fun scheduledExecutorService(threadName: String?, threadCount: Int): ScheduledExecutorService {
        return ScheduledThreadPoolExecutor(
            threadCount,
            NamedThreadFactory(threadName)
        ).apply {
            maximumPoolSize = threadCount
        }
    }
}