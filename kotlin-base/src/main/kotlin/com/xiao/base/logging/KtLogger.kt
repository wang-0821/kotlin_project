package com.xiao.base.logging

import com.xiao.base.annotation.AnnotationScan

/**
 *
 * @author lix wang
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@AnnotationScan
annotation class KtLogger(
    val value: LoggerType = LoggerType.NULL,
    val name: String = ""
)