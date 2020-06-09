package com.xiao.base.annotation

import kotlin.reflect.KClass

/**
 *
 * @author lix wang
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@AnnotationScan
annotation class Component(
    val value: String = "",
    val handler: KClass<out AnnotationHandler> = ComponentResourceHandler::class
)