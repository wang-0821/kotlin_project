package com.xiao.base.executor

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.withTimeout
import java.util.concurrent.TimeUnit
import kotlin.coroutines.cancellation.CancellationException

/**
 *
 * @author lix wang
 */
class SafeCompletableDeferred<T : Any?>(
    private val deferred: CompletableDeferred<T>
) : SafeDeferred<T> {
    private lateinit var job: Job

    fun putJob(job: Job) {
        this.job = job
        job.cancel()
    }

    override suspend fun await(): T {
        return deferred.await()
    }

    override suspend fun awaitNanos(timeout: Long, timeUnit: TimeUnit): T {
        return withTimeout(timeUnit.toMillis(timeout)) {
            await()
        }
    }

    override fun cancel(cause: CancellationException?) {
        job.cancel(cause)
        deferred.cancel(cause)
    }

    @ExperimentalCoroutinesApi
    override fun getCompleted(): T {
        return deferred.getCompleted()
    }

    override val isCompleted: Boolean
        get() = deferred.isCompleted
}