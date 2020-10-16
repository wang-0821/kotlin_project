package com.xiao.base.annotation

import com.xiao.base.resource.KtClassResource
import kotlin.reflect.KClass

/**
 *
 * @author lix wang
 */
class AnnotatedKtResource(val classResource: KtClassResource, val annotations: List<Annotation>) {
    fun <T : Annotation> isAnnotated(annotation: KClass<T>): Boolean {
        return annotations.any { it.annotationClass == annotation }
    }

    fun <T : Annotation> annotationsByType(annotation: KClass<T>): List<T> {
        @Suppress("UNCHECKED_CAST")
        return annotations.asSequence().filter { it.annotationClass == annotation }.map { it as T }.toList()
    }
}