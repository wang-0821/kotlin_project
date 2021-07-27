package com.xiao.boot.base.thread

import com.xiao.base.executor.DefaultExecutorServiceFactory

/**
 * @author lix wang
 */
object KtThreadPool {
    val workerPool = run {
        val workerThreads = Runtime.getRuntime().availableProcessors() * 8
        return@run DefaultExecutorServiceFactory.newExecutorService("undertow-http-thread", workerThreads)
    }
}