package com.xiao.base.context

import com.xiao.base.annotation.AnnotationScanner
import com.xiao.base.exception.HttpStatus
import com.xiao.base.exception.KtException
import com.xiao.base.resource.KtResource
import com.xiao.base.resource.PathResourceResolver

/**
 *
 * @author lix wang
 */
object ContextScanner : BeanRegistryAware {
    fun doScan(basePackage: String) {
        val resources = PathResourceResolver().scanByPackage(basePackage)
        for (resource in resources) {
            filterResource(resource)
        }
    }

    private fun filterResource(resource: KtResource): List<KtResourceProcessor> {
        val result = mutableListOf<KtResourceProcessor>()
        val annotations = resource.clazz.java.extractAnnotations()
        annotations.firstOrNull { it.annotationClass == AnnotationScanner::class }?.let {
            val annotationScanner = it as AnnotationScanner
            if (!annotationScanner.typeFilter.isCompanion) {
                throw KtException().statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                    .errorCode("context.invalid_typeFilter")
                    .message("${annotationScanner.typeFilter.simpleName} shuold be companion object.")
            } else {
                if (annotationScanner.typeFilter.objectInstance!!(resource)) {
                    result.add(KtResourceProcessor(resource, annotations))
                }
            }
        }
        return result
    }

    private fun Class<*>.extractAnnotations(): List<Annotation> {
        val result = mutableListOf<Annotation>()
        return this.extractAnnotations(result)
    }

    private fun Class<*>.extractAnnotations(result: MutableList<Annotation>): List<Annotation> {
        var annotations = this::class.java.annotations
        result.addAll(annotations)
        for (annotation in annotations) {
            annotation.annotationClass.java.extractAnnotations(result)
        }
        return result
    }

    private class KtResourceProcessor(
        val resource: KtResource,
        val annotations: List<Annotation>,
        var processed: Boolean = false
    )
}