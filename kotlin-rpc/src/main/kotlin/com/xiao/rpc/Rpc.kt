package com.xiao.rpc

import com.xiao.base.executor.AsyncUtil
import com.xiao.base.executor.QueueItem
import com.xiao.base.logging.Logging
import com.xiao.rpc.io.Request
import com.xiao.rpc.io.Response
import com.xiao.rpc.util.UrlParser
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
object Rpc: Logging() {
    val client = Client()
    var started = AtomicInteger(0)

    fun  call(name: String, request: Request): Future<Response> {
        return AsyncUtil.executor.submit(queueItem(name, request))
    }

    suspend fun CoroutineScope.deferred(name: String, request: Request): Deferred<Response> {
        return withContext(this.coroutineContext) {
            async {
                queueItem(name, request).call()
            }
        }
    }

    suspend fun <T> Deferred<T>.get(timeout: Long, timeUnit: TimeUnit): T {
        val deferred = this
        return withTimeout(timeUnit.toMillis(timeout)) {
            deferred.await()
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

fun main() {
    val request = UrlParser.parseUrl("https://www.baidu.com")
}