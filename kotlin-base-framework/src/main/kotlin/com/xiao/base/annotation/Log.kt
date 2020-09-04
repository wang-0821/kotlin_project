package com.xiao.base.annotation

/**
 *
 * @author lix wang
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@Component
annotation class Log(
    val value: String = ""
)