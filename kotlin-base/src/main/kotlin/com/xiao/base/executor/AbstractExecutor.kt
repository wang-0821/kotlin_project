package com.xiao.base.executor

import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService

/**
 *
 * @author lix wang
 */
abstract class AbstractExecutor(
    val name: String,
    executorService: ExecutorService
) : AbstractExecutorService(executorService) {
    @Suppress("UNCHECKED_CAST")
    open fun <T> execute(task: () -> T): CompletableFuture<T> {
        val completableFuture = SafeCompletableFuture<T>()
        val runnable = CompletableCallback(
            task,
            completableFuture as CompletableFuture<Any?>
        )
        val future = executorService.submit {
            runnable.run()
        }
        completableFuture.putFuture(future)
        return completableFuture
    }
}