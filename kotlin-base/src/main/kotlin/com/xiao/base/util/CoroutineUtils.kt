package com.xiao.base.util

import com.xiao.base.executor.CoroutineCompletableCallback
import com.xiao.base.executor.SafeCompletableDeferred
import com.xiao.base.executor.SafeDeferred
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 *
 * @author lix wang
 */
@Suppress("UNCHECKED_CAST")
fun <T : Any?> CoroutineScope.deferred(block: () -> T): SafeDeferred<T> {
    val deferred = CompletableDeferred<T>()
    val result = SafeCompletableDeferred(deferred)
    val job = launch {
        CoroutineCompletableCallback(block, null, deferred as CompletableDeferred<Any?>).run()
    }
    result.putJob(job)
    return result
}

@Suppress("UNCHECKED_CAST")
fun <T : Any?> CoroutineScope.deferredSuspend(suspendBlock: suspend () -> T): SafeDeferred<T> {
    val deferred = CompletableDeferred<T>()
    val result = SafeCompletableDeferred(deferred)
    val job = launch {
        CoroutineCompletableCallback(null, suspendBlock, deferred as CompletableDeferred<Any?>).suspendRun()
    }
    result.putJob(job)
    return result
}