package com.xiao.base.annotation

/**
 *
 * @author lix wang
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@AnnotationScanner
annotation class Component(
    val value: String
)