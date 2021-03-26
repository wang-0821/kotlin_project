package com.xiao.base.executor

import com.xiao.base.CommonConstants.DEFAULT_EXECUTION_TIMEOUT
import java.util.concurrent.TimeUnit
import kotlin.coroutines.cancellation.CancellationException

/**
 *
 * @author lix wang
 */
interface SafeDeferred<T : Any?> {
    suspend fun awaitNanos(timeout: Long = DEFAULT_EXECUTION_TIMEOUT, timeUnit: TimeUnit = TimeUnit.MILLISECONDS): T

    fun cancel(cause: CancellationException? = null)

    fun getCompleted(): T

    val isCompleted: Boolean

    val isCanceled: Boolean
}