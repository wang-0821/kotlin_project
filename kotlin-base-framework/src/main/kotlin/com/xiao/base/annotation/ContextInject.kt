package com.xiao.base.annotation

import kotlin.reflect.KClass

/**
 * First executed annotation.
 *
 * @author lix wang
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@AnnotationScanner
annotation class ContextInject(
    val handler: KClass<out AnnotationHandler> = ContextInjectResourceHandler::class
)