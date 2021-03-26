package com.xiao.base.executor

import com.xiao.base.CommonConstants.DEFAULT_EXECUTION_TIMEOUT
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

    /**
     * If task is running and [mayInterruptIfRunning] is false, then won't really cancel task.
     * If task is running and [mayInterruptIfRunning] is true, then will call [Thread.interrupt] end the task.
     * [Thread.interrupt] will throws [InterruptedException] while thread is blocking(sleep, wait, join).
     * [Thread.interrupt] will only set interrupted flag as true while thread is running, will not break thread.
     *
     * So, suggest call with [mayInterruptIfRunning] false.
     */
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

    /**
     * Delegate to get result with timeout.
     * Avoid dead lock.
     */
    @Throws(
        InterruptedException::class,
        ExecutionException::class,
        TimeoutException::class
    )
    override fun get(): T {
        return get(DEFAULT_EXECUTION_TIMEOUT, TimeUnit.MILLISECONDS)
    }
}