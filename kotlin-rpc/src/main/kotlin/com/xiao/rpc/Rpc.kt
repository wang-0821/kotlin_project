package com.xiao.rpc

import com.xiao.base.executor.AsyncUtil
import com.xiao.base.executor.QueueItem
import com.xiao.rpc.io.Request
import com.xiao.rpc.io.Response
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.apache.logging.log4j.ThreadContext
import java.util.UUID
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

/**
 *
 * @author lix wang
 */
object Rpc {
    val client = Client()
    var started = AtomicInteger(0)

    fun  future(name: String, request: Request): Future<Response> {
        return AsyncUtil.executor.submit(queueItem(name, request))
    }

    fun sync(name: String, request: Request): Response {
        return queueItem(name, request).call()
    }

    suspend fun deferred(
        name: String,
        request: Request,
        coroutineScope: CoroutineScope = AsyncUtil.coroutineScope
    ): Deferred<Response> {
        return withContext(coroutineScope.coroutineContext) {
            async {
                queueItem(name, request).call()
            }
        }
    }

    private fun queueItem(name: String, request: Request): QueueItem<Response> {
        return object : QueueItem<Response>(name) {
            override fun execute(): Response {
                return client.newCall(request).execute()
            }

            override fun call(): Response {
                started.getAndIncrement()
                ThreadContext.put("RpcRequestId", UUID.randomUUID().toString())
                val result = super.call()
                ThreadContext.clearMap()
                started.getAndDecrement()
                return result
            }
        }
    }
}

suspend fun <T> Deferred<T>.result(timeout: Long = 60000, timeUnit: TimeUnit = TimeUnit.MILLISECONDS): T {
    val deferred = this
    return withTimeout(timeUnit.toMillis(timeout)) {
        deferred.await()
    }
}