package com.xiao.rpc

import com.xiao.base.executor.ExecutionQueue
import com.xiao.base.executor.QueueItem
import com.xiao.base.executor.SafeDeferred
import com.xiao.base.util.ThreadUtils
import com.xiao.base.util.deferred
import com.xiao.rpc.io.Request
import com.xiao.rpc.io.Response
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import org.apache.logging.log4j.ThreadContext
import java.util.UUID
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicInteger

/**
 *
 * @author lix wang
 */
object Rpc {
    private val executionQueue = ExecutionQueue("Rpc-execution-queue", ThreadUtils.DEFAULT_EXECUTOR)
    val client = Client()
    var started = AtomicInteger(0)

    @JvmStatic
    fun async(name: String, request: Request): CompletableFuture<Response> {
        return executionQueue.submit {
            queueItem(name, request).call()
        }
    }

    @JvmStatic
    fun sync(name: String, request: Request): Response {
        return queueItem(name, request).call()
    }

    @JvmOverloads
    suspend fun deferred(
        name: String,
        request: Request,
        scope: CoroutineScope? = null
    ): SafeDeferred<Response> {
        return scope?.coroutineContext?.let {
            withContext(it) {
                deferred {
                    queueItem(name, request).call()
                }
            }
        } ?: kotlin.run {
            coroutineScope {
                deferred {
                    queueItem(name, request).call()
                }
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
                ThreadContext.put("X-RequestId", UUID.randomUUID().toString())
                val result = super.call()
                ThreadContext.clearMap()
                started.getAndDecrement()
                return result
            }
        }
    }
}