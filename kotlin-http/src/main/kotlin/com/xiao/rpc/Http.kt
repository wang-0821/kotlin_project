package com.xiao.rpc

import com.xiao.base.executor.ExecutionQueue
import com.xiao.base.executor.SafeDeferred
import com.xiao.base.util.ThreadUtils
import com.xiao.base.util.deferred
import com.xiao.rpc.io.Request
import com.xiao.rpc.io.Response
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicInteger

/**
 *
 * @author lix wang
 */
object Http {
    private val executionQueue = ExecutionQueue("Rpc-execution-queue", ThreadUtils.DEFAULT_EXECUTOR)
    val client = Client()
    var started = AtomicInteger(0)

    @JvmStatic
    fun async(request: Request): CompletableFuture<Response> {
        return executionQueue.submit {
            doRequest(request)
        }
    }

    @JvmStatic
    fun sync(request: Request): Response {
        return doRequest(request)
    }

    @JvmOverloads
    suspend fun deferred(
        request: Request,
        scope: CoroutineScope? = null
    ): SafeDeferred<Response> {
        return scope?.coroutineContext?.let {
            withContext(it) {
                deferred {
                    doRequest(request)
                }
            }
        } ?: kotlin.run {
            coroutineScope {
                deferred {
                    doRequest(request)
                }
            }
        }
    }

    private fun doRequest(request: Request): Response {
        started.getAndIncrement()
        val result = client.newCall(request).execute()
        started.getAndDecrement()
        return result
    }
}