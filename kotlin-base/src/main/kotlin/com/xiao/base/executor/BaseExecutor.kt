package com.xiao.base.executor

import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService

/**
 *
 * @author lix wang
 */
@Suppress("unused")
abstract class BaseExecutor(
    val name: String,
    private val executorService: ExecutorService
) : ExecutorMonitor {
    @Suppress("UNCHECKED_CAST")
    open fun <T : Any?> execute(task: () -> T): CompletableFuture<T> {
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

    open fun shutdown() {
        executorService.shutdown()
    }

    open fun shutdownNow() {
        executorService.shutdownNow()
    }

    open fun isShutdown(): Boolean {
        return executorService.isShutdown
    }
}