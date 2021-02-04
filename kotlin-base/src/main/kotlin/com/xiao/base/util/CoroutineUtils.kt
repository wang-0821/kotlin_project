package com.xiao.base.util

import com.xiao.base.executor.CompletableCallback
import com.xiao.base.executor.SafeCompletableDeferred
import com.xiao.base.executor.SafeDeferred
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 *
 * @author lix wang
 */
@Suppress("UNCHECKED_CAST")
fun <T : Any?> CoroutineScope.deferred(block: () -> T): SafeDeferred<T> {
    val result = SafeCompletableDeferred<T>()
    val job = launch {
        CompletableCallback(block, null, result as CompletableDeferred<Any?>).run()
    }
    result.putJob(job)
    return result
}

@Suppress("UNCHECKED_CAST")
fun <T : Any?> CoroutineScope.deferredSuspend(block: suspend () -> T): SafeDeferred<T> {
    val result = SafeCompletableDeferred<T>()
    val job = launch {
        CompletableCallback({ callSuspend { block() } }, null, result as CompletableDeferred<Any?>).run()
    }
    result.putJob(job)
    return result
}

private fun <T : Any?> CoroutineScope.callSuspend(block: suspend () -> T): T {
    return runBlocking(this.coroutineContext) {
        block()
    }
}