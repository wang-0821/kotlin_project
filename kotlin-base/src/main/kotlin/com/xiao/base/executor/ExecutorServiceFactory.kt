package com.xiao.base.executor

import java.util.concurrent.ExecutorService
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 *
 * @author lix wang
 */
object ExecutorServiceFactory {
    @JvmStatic
    fun newDefaultThreadPoolExecutor(threadCount: Int): ExecutorService {
        return ThreadPoolExecutor(
            threadCount,
            threadCount,
            0,
            TimeUnit.SECONDS,
            LinkedBlockingDeque<Runnable>(),
            NamedThreadFactory()
        )
    }
}