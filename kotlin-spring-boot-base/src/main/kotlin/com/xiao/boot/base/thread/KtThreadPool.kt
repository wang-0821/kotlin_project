package com.xiao.boot.base.thread

import com.xiao.base.executor.DefaultExecutorServiceFactory
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.ExecutorService
import java.util.concurrent.ThreadPoolExecutor

/**
 * @author lix wang
 */
object KtThreadPool {
    val globalPool = run {
        val workerThreads = Runtime.getRuntime().availableProcessors() * 8
        val executor = DefaultExecutorServiceFactory.newExecutorService("undertow-http-thread", workerThreads)
        Runtime.getRuntime().addShutdownHook(Thread { executor.fastShutDown() })
        return@run executor
    }

    val globalCoroutineContext = globalPool.asCoroutineDispatcher()
}

fun ExecutorService.fastShutDown() {
    shutdown()
    if (this is ThreadPoolExecutor) {
        while (this.queue.poll() == null) {
            break
        }
    }
}