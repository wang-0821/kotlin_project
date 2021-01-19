package com.xiao.redis.client

import com.xiao.base.executor.CompletableCallback
import io.lettuce.core.RedisFuture
import kotlinx.coroutines.CompletableDeferred

/**
 *
 * @author lix wang
 */
@Suppress("UNCHECKED_CAST")
fun <T : Any?> RedisFuture<T>.suspend(): CompletableDeferred<T> {
    val result = CompletableDeferred<T>()
    whenComplete { value, throwable ->
        throwable?.also {
            throw it
        }
        CompletableCallback({ value }, null, result as CompletableDeferred<Any?>).run()
    }
    return result
}