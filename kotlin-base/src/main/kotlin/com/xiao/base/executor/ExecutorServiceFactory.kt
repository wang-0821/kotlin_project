package com.xiao.base.executor

import java.util.concurrent.ExecutorService
import java.util.concurrent.ScheduledExecutorService

/**
 *
 * @author lix wang
 */
interface ExecutorServiceFactory {
    fun newExecutorService(threadCount: Int): ExecutorService

    fun newExecutorService(threadName: String, threadCount: Int): ExecutorService

    fun newScheduledExecutorService(threadCount: Int): ScheduledExecutorService

    fun newScheduledExecutorService(threadName: String, threadCount: Int): ScheduledExecutorService
}