package com.xiao.base.logging

import com.xiao.base.annotation.Log
import org.apache.logging.log4j.LogManager
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 *
 * @author lix wang
 */
abstract class Logging {
    val log = LogManager.getLogger(this::class.java)

    private fun logger(): Logger {
        val loggerAnnotation = this::class.java.getAnnotation(Log::class.java)
        return if (loggerAnnotation != null && loggerAnnotation.value.isNotBlank()) {
            LoggerFactory.getLogger(loggerAnnotation.value)
        } else {
            LoggerFactory.getLogger(this::class.java)
        }
    }
}

class DemoLogging: Logging() {
}

fun main() {
    val de = DemoLogging()
    de.log.error("Hello")
}