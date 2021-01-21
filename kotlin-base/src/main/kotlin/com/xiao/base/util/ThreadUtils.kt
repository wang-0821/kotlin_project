package com.xiao.base.util

import com.xiao.base.logging.Logging

/**
 *
 * @author lix wang
 */
object ThreadUtils : Logging() {
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