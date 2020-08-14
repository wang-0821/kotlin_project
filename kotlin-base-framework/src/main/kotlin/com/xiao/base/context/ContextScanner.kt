package com.xiao.base.context

import com.xiao.base.annotation.AnnotatedKtResource
import com.xiao.base.annotation.AnnotationScan
import com.xiao.base.annotation.Component
import com.xiao.base.annotation.ContextInject
import com.xiao.base.resource.KtResource
import com.xiao.base.resource.PathResourceScanner

/**
 *
 * @author lix wang
 */
object ContextScanner : BeanRegistryAware {
    @Volatile
    private var refreshed = false

    @Synchronized fun scanAndExecute(basePackage: String) {
        if (refreshed) {
            return
        }
        handleResourceProcessors(scan(basePackage))
        refreshed = true
    }

    private fun scan(basePackage: String): List<AnnotatedKtResource> {
        val resources = PathResourceScanner().scanByPackage(basePackage)
        val annotationResources = mutableListOf<AnnotatedKtResource>()
        for (resource in resources) {
            filterResource(resource)?.let {
                annotationResources.add(it)
            }
        }
        return annotationResources
    }

    private fun handleResourceProcessors(annotatedKtResources: List<AnnotatedKtResource>) {
        handleContextInject(annotatedKtResources)?.let {
            handleComponentProcessor(it)
        }
    }

    private fun handleContextInject(annotatedKtResources: List<AnnotatedKtResource>): List<AnnotatedKtResource>? {
        val contextInjectResources = annotatedKtResources.filter { it.isAnnotated(ContextInject::class) }
        for (resource in contextInjectResources) {
            val contextInject = resource.annotationsByType(ContextInject::class).first()
            val handler = contextInject.handler.objectInstance ?: contextInject.handler.objectInstance
            handler?.let {
                it(resource)
            }
        }
        return annotatedKtResources.filterNot { contextInjectResources.contains(it) }
    }

    private fun handleComponentProcessor(annotatedKtResources: List<AnnotatedKtResource>) {
        val componentResources = annotatedKtResources.filter { it.isAnnotated(Component::class) }
        for (resource in componentResources) {
            val component = resource.annotationsByType(Component::class).first()
            val handler = component.handler.objectInstance ?: component.handler.objectInstance
            handler?.let {
                it(resource)
            }
        }
    }

    private fun filterResource(resource: KtResource): AnnotatedKtResource? {
        val annotations = resource.clazz.java.extractAnnotations()
        annotations.firstOrNull { it.annotationClass == AnnotationScan::class }?.let {
            val annotationScan = it as AnnotationScan
            val includeTypeFilter = annotationScan.includeTypeFilter.objectInstance
                ?: annotationScan.includeTypeFilter.objectInstance
            val excludeTypeFilter = annotationScan.excludeTypeFilter.objectInstance
                ?: annotationScan.excludeTypeFilter.objectInstance
            if (excludeTypeFilter != null && excludeTypeFilter(resource)) {
                return null
            }
            if (includeTypeFilter != null && includeTypeFilter(resource)) {
                return AnnotatedKtResource(resource, annotations)
            }
        }
        return null
    }

    private fun <T : Class<out Any>> T.extractAnnotations(): List<Annotation> {
        val result = mutableListOf<Annotation>()
        return this.extractAnnotations(result)
    }

    private fun <T : Class<out Any>> T.extractAnnotations(result: MutableList<Annotation>): List<Annotation> {
        var annotations = this.annotations.filter { !EXCLUDE_META_ANNOTATIONS.contains(it.annotationClass) }
        result.addAll(annotations)
        for (annotation in annotations) {
            annotation.annotationClass.java.extractAnnotations(result)
        }
        return result
    }

    private val EXCLUDE_META_ANNOTATIONS = listOf(
        Metadata::class,
        Retention::class,
        Target::class,
        java.lang.annotation.Retention::class,
        java.lang.annotation.Target::class
    )
}