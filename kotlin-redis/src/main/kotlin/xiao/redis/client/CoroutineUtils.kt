package xiao.redis.client

import io.lettuce.core.RedisFuture
import kotlinx.coroutines.CompletableDeferred
import xiao.base.executor.CoroutineCompletableCallback
import xiao.base.executor.SafeCompletableDeferred
import xiao.base.executor.SafeDeferred

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
        CoroutineCompletableCallback({ value }, null, deferred as CompletableDeferred<Any?>).run()
    }
    return result
}