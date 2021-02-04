package com.xiao.base.executor

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.withTimeout
import java.util.concurrent.TimeUnit
import kotlin.coroutines.cancellation.CancellationException

/**
 *
 * @author lix wang
 */
@Suppress("UNCHECKED_CAST")
class SafeCompletableDeferred<T: Any?> : SafeDeferred<T> {
    private val completableDeferred: CompletableDeferred<T> = CompletableDeferred()
    private lateinit var job: Job

    fun putJob(job: Job) {
        this.job = job
        job.cancel()
    }

    override suspend fun await(): T {
        return completableDeferred.await()
    }

    override suspend fun awaitNanos(timeout: Long, timeUnit: TimeUnit): T {
        return withTimeout(timeUnit.toMillis(timeout)) {
            await()
        }
    }

    override fun cancel(cause: CancellationException?) {
        job.cancel(cause)
        completableDeferred.cancel(cause)
    }
}