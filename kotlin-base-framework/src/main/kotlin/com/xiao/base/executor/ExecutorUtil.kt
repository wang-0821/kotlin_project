package com.xiao.base.executor

import java.util.concurrent.Callable

/**
 *
 * @author lix wang
 */
object ExecutorUtil {
    private val executorService = ExecutorServiceFactory.newDefaultThreadPoolExecutor(4)
    private val executor = ExecutionQueue("AsyncExecutionQueue", executorService)

    fun<T> submit(name: String, callable: Callable<T>): WrappedFuture<T> {
        return executor.submit(name, callable)
    }

    fun<T> submit(callable: Callable<T>): WrappedFuture<T> {
        return executor.submit(callable)
    }
}