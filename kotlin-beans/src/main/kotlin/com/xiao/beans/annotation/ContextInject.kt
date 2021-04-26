package com.xiao.beans.annotation

import kotlin.reflect.KClass

/**
 * This annotation means annotated class is a context.
 *
 * @author lix wang
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@AnnotationScan
annotation class ContextInject(
    val handler: KClass<out AnnotationHandler> = ContextInjectResourceHandler::class
)