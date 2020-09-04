package com.xiao.base.annotation

/**
 *
 * @author lix wang
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@AnnotationScan
annotation class Log(
    val value: String = ""
)