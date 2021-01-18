package com.xiao.base.executor

import com.xiao.base.executor.QueueItemHelper.getQueueItem
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.concurrent.Callable

/**
 *
 * @author lix wang
 */
@Suppress("UNCHECKED_CAST")
fun <T : Any?> CoroutineScope.deferred(taskName: String?, callable: Callable<T>): CompletableDeferred<T> {
    val name = if (taskName.isNullOrBlank()) {
        "Queue-Deferred"
    } else taskName

    val queueItem = getQueueItem(name, callable) as QueueItem<Any?>
    val result = CompletableDeferred<Any?>()
    launch {
        CompletableCallback(queueItem, null, result).run()
    }
    return result as CompletableDeferred<T>
}

fun <T : Any?> CoroutineScope.deferred(callable: Callable<T>): CompletableDeferred<T> {
    return deferred(null, callable)
}