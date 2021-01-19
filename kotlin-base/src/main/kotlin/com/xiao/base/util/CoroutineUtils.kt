package com.xiao.base.util

import com.xiao.base.executor.CompletableCallback
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import java.util.concurrent.Callable
import java.util.concurrent.TimeUnit

/**
 *
 * @author lix wang
 */
@Suppress("UNCHECKED_CAST")
fun <T : Any?> CoroutineScope.deferred(callable: Callable<T>): CompletableDeferred<T> {
    val result = CompletableDeferred<Any?>()
    launch {
        CompletableCallback({ callable.call() }, null, result).run()
    }
    return result as CompletableDeferred<T>
}

@Suppress("UNCHECKED_CAST")
fun <T : Any?> CoroutineScope.deferred(block: suspend () -> T): CompletableDeferred<T> {
    val result = CompletableDeferred<Any?>()
    launch {
        CompletableCallback({ callSuspend { block() } }, null, result).run()
    }
    return result as CompletableDeferred<T>
}

fun <T : Any?> CoroutineScope.callSuspend(block: suspend () -> T): T {
    return runBlocking(this.coroutineContext) {
        block()
    }
}

suspend fun <T> Deferred<T>.awaitNanos(timeout: Long = 60000, timeUnit: TimeUnit = TimeUnit.MILLISECONDS): T {
    return withTimeout(timeUnit.toMillis(timeout)) {
        this@awaitNanos.await()
    }
}