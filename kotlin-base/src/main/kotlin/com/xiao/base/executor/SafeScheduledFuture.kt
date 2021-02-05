package com.xiao.base.executor

import java.util.concurrent.Delayed
import java.util.concurrent.Future
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

/**
 *
 * @author lix wang
 */
class SafeScheduledFuture<T : Any?> : SafeCompletableFuture<T>(), ScheduledFuture<T> {
    override fun putFuture(future: Future<*>) {
        if (future is ScheduledFuture) {
            super.putFuture(future)
        } else {
            throw IllegalArgumentException("Must be ${ScheduledFuture::class.java.simpleName}.")
        }
    }

    override fun compareTo(other: Delayed?): Int {
        return (future as ScheduledFuture).compareTo(other)
    }

    override fun getDelay(unit: TimeUnit): Long {
        return (future as ScheduledFuture).getDelay(unit)
    }
}