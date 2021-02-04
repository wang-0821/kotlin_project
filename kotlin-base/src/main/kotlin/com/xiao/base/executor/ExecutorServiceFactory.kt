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
object ExecutorServiceFactory {
    @JvmStatic
    fun newThreadPoolExecutor(threadCount: Int): ExecutorService {
        return ThreadPoolExecutor(
            threadCount,
            threadCount,
            0,
            TimeUnit.SECONDS,
            LinkedBlockingDeque(),
            NamedThreadFactory("KThread")
        )
    }

    @JvmStatic
    fun newScheduledExecutorService(threadCount: Int, ): ScheduledExecutorService {
        return ScheduledThreadPoolExecutor(
            threadCount,
            NamedThreadFactory("KScheduledThread")
        ).apply {
            maximumPoolSize = threadCount
        }
    }
}