package com.xiao.base.executor

import java.util.concurrent.CompletableFuture
import java.util.concurrent.Future

/**
 *
 * @author lix wang
 */
class SafeCompletableFuture<T : Any?> : CompletableFuture<T>() {
    private lateinit var future: Future<*>

    fun putFuture(future: Future<*>) {
        this.future = future
    }

    override fun cancel(mayInterruptIfRunning: Boolean): Boolean {
        future.cancel(mayInterruptIfRunning)
        return super.cancel(mayInterruptIfRunning)
    }
}