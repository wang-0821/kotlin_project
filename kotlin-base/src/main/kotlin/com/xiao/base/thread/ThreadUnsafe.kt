package com.xiao.base.thread

/**
 * Mark annotated target is thread unsafe.
 *
 * @author lix wang
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.ANNOTATION_CLASS,
    AnnotationTarget.FIELD,
    AnnotationTarget.FUNCTION
)
annotation class ThreadUnsafe(
    val description: String = ""
)