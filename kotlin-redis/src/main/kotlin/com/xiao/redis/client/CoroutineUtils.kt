package com.xiao.redis.client

import com.xiao.base.executor.CompletableCallback
import io.lettuce.core.RedisFuture
import kotlinx.coroutines.CompletableDeferred

/**
 * @author lix wang
 */
// add suspend modifier, because we expect this method used in coroutine.
@Suppress("UNCHECKED_CAST", "RedundantSuspendModifier")
suspend fun <T : Any?> RedisFuture<T>.suspend(): CompletableDeferred<T> {
    val result = CompletableDeferred<T>()
    whenComplete { value, throwable ->
        throwable?.also {
            throw it
        }
        CompletableCallback({ value }, null, result as CompletableDeferred<Any?>).run()
    }
    return result
}