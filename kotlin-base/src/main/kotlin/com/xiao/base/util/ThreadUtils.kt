package com.xiao.base.util

import com.xiao.base.executor.DefaultExecutorServiceFactory
import com.xiao.base.logging.Logging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlin.coroutines.CoroutineContext

/**
 *
 * @author lix wang
 */
object ThreadUtils : Logging() {
    @JvmField
    val DEFAULT_EXECUTOR = DefaultExecutorServiceFactory.newExecutorService(8)

    @JvmField
    val DEFAULT_SCOPE: CoroutineScope = object : CoroutineScope {
        override val coroutineContext: CoroutineContext
            get() = DEFAULT_EXECUTOR.asCoroutineDispatcher()
    }

    @JvmStatic
    fun safeSleep(mills: Long): Boolean {
        return try {
            Thread.sleep(mills)
            true
        } catch (e: Exception) {
            Thread.currentThread().interrupt()
            log.error("Thread sleep failed, ${e.message}.", e)
            false
        }
    }

    @JvmStatic
    fun rootPath(): String {
        return Thread.currentThread().contextClassLoader.getResource("")?.path ?: ""
    }
}