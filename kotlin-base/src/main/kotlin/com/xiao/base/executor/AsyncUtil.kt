package com.xiao.base.executor

import kotlinx.coroutines.asCoroutineDispatcher

/**
 *
 * @author lix wang
 */
object AsyncUtil {
    private val executorService = ExecutorServiceFactory.newDefaultThreadPoolExecutor(8)
    val executor = ExecutionQueue("AsyncExecutionQueue", executorService)
    val dispatcher = executorService.asCoroutineDispatcher()
}