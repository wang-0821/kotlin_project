package com.xiao.base.executor

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlin.coroutines.CoroutineContext

/**
 *
 * @author lix wang
 */
object AsyncUtil {
    private val executorService = ExecutorServiceFactory.newDefaultThreadPoolExecutor(8)
    private val dispatcher = executorService.asCoroutineDispatcher()
    val executor = ExecutionQueue("AsyncExecutionQueue", executorService)
    val coroutineScope: CoroutineScope = object : CoroutineScope {
        override val coroutineContext: CoroutineContext
            get() = dispatcher
    }
}