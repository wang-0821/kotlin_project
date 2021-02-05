package com.xiao.base.executor

import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

/**
 *
 * @author lix wang
 */
open class SafeCompletableFuture<T : Any?> : CompletableFuture<T>() {
    protected lateinit var future: Future<*>

    open fun putFuture(future: Future<*>) {
        this.future = future
    }

    override fun cancel(mayInterruptIfRunning: Boolean): Boolean {
        future.cancel(mayInterruptIfRunning)
        return super.cancel(mayInterruptIfRunning)
    }

    @Throws(
        InterruptedException::class,
        ExecutionException::class,
        TimeoutException::class
    )
    override fun get(timeout: Long, unit: TimeUnit): T {
        try {
            return super.get(timeout, unit)
        } catch (e: Exception) {
            future.cancel(false)
            throw e
        }
    }
}