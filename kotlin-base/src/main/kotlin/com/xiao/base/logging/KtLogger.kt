package com.xiao.base.logging

/**
 *
 * @author lix wang
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class KtLogger(
    val value: LoggerType = LoggerType.UNDEFINED,
    val name: String = ""
)