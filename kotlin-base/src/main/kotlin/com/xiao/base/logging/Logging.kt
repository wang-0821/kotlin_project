package com.xiao.base.logging

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
        val annotation = this::class.java.getAnnotation(KtLogger::class.java) ?: return this::class.java.name
        if (annotation.value != LoggerType.NULL) {
            return annotation.value.text
        }
        if (annotation.name.isNotBlank()) {
            return annotation.name
        }

        return this::class.java.name
    }
}