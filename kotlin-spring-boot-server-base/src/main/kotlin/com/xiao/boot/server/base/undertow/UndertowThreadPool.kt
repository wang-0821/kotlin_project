package com.xiao.boot.server.base.undertow

import com.xiao.boot.base.thread.KtThreadPool
import io.undertow.util.AttachmentKey
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.ExecutorService
import kotlin.coroutines.CoroutineContext

/**
 * @author lix wang
 */
object UndertowThreadPool {
    val UNDERTOW_THREAD_POOL_ATTACHMENT: AttachmentKey<ExecutorService> =
        AttachmentKey.create(ExecutorService::class.java)

    val GLOBAL_COROUTINE_CONTEXT: CoroutineContext = KtThreadPool.workerPool.asCoroutineDispatcher()
}