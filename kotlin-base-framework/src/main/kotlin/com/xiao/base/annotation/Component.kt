package com.xiao.base.annotation

import kotlin.reflect.KClass

/**
 *
 * @author lix wang
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@AnnotationScanner
annotation class Component(
    val value: String = "",
    val handler: KClass<out AnnotationHandler> = ComponentResourceHandler::class
)