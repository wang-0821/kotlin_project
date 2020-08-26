package com.xiao.base.executor

import java.util.concurrent.ExecutorService

/**
 *
 * @author lix wang
 */
object ExecutorUtil {
    private val factory = ExecutorServiceFactory()

    fun defaultExecutorService(): ExecutorService {
        return factory.newDefaultThreadPoolExecutor(4)
    }
}