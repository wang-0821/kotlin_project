package com.xiao.redis.client

import com.xiao.base.executor.CompletableCallback
import com.xiao.base.executor.SafeCompletableDeferred
import com.xiao.base.executor.SafeDeferred
import io.lettuce.core.RedisFuture
import kotlinx.coroutines.CompletableDeferred

/**
 * @author lix wang
 */
// add suspend modifier, because we expect this method used in coroutine.
@Suppress("UNCHECKED_CAST", "RedundantSuspendModifier")
suspend fun <T : Any?> RedisFuture<T>.suspend(): SafeDeferred<T> {
    val deferred = CompletableDeferred<T>()
    val result = SafeCompletableDeferred(deferred)
    whenComplete { value, throwable ->
        throwable?.also {
            throw it
        }
        CompletableCallback({ value }, null, deferred as CompletableDeferred<Any?>).run()
    }
    return result
}