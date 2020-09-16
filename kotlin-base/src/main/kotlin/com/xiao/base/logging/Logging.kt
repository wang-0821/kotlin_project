package com.xiao.base.logging

import com.xiao.base.annotation.Log
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.helpers.NOPLogger
import org.slf4j.helpers.Util

/**
 *
 * @author lix wang
 */
abstract class Logging {
    val log = logger()

    private fun logger(): Logger {
        val loggerName = loggerName()
        val logger = LoggerFactory.getLogger(loggerName)
        if (logger == NOPLogger.NOP_LOGGER) {
            Util.report("There is no available logger named {$loggerName}, please implement it.")
        }
        return logger
    }

    private fun loggerName(): String {
        val loggerAnnotation = this::class.java.getAnnotation(Log::class.java)
        return if (loggerAnnotation != null && loggerAnnotation.value.isNotBlank()) {
            loggerAnnotation.value
        } else {
            this::class.java.name
        }
    }
}