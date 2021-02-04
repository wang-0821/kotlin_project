package com.xiao.redis.utils

import java.util.concurrent.CompletableFuture
import java.util.concurrent.ScheduledFuture

/**
 *
 * @author lix wang
 */
class CompletableScheduledFuture<T : Any?>(
    private val scheduledFuture: ScheduledFuture<T>
) : CompletableFuture<T>() {
    override fun cancel(mayInterruptIfRunning: Boolean): Boolean {
        scheduledFuture.cancel(mayInterruptIfRunning)
        return super.cancel(mayInterruptIfRunning)
    }
}