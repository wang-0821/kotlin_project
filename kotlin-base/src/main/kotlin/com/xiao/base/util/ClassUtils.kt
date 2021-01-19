package com.xiao.base.util

import com.xiao.base.annotation.KtRetry
import java.lang.reflect.Method
import kotlin.reflect.KClass

/**
 *
 * @author lix wang
 */
fun KClass<*>.packageName(): String {
    val name = this.java.name
    val lastDotIndex: Int = name.lastIndexOf(".")
    return if (lastDotIndex != -1) name.substring(0, lastDotIndex) else ""
}

fun <T : Class<out Any>> T.extractAnnotations(): List<Annotation> {
    val result = mutableListOf<Annotation>()
    return this.extractAnnotations(result)
}

fun Method.extractRetryTimes(): Int {
    val retryAnnotation = this.getAnnotation(KtRetry::class.java)
        ?: this::class.java.getAnnotation(KtRetry::class.java)
    return retryAnnotation?.times ?: 0
}

private fun <T : Class<out Any>> T.extractAnnotations(result: MutableList<Annotation>): List<Annotation> {
    val annotations = this.annotations.filter { !result.contains(it) }
    result.addAll(annotations)
    for (annotation in annotations) {
        annotation.annotationClass.java.extractAnnotations(result)
    }
    return result
}