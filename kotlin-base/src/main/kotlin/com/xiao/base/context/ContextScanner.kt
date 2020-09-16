package com.xiao.base.context

import com.xiao.base.annotation.AnnotatedKtResource
import com.xiao.base.annotation.AnnotationScan
import com.xiao.base.annotation.Component
import com.xiao.base.annotation.ContextInject
import com.xiao.base.annotation.extractAnnotations
import com.xiao.base.resource.KtResource
import com.xiao.base.resource.PathResourceScanner

/**
 *
 * @author lix wang
 */
object ContextScanner : BeanRegistryAware {
    fun scanAnnotatedResources(basePackage: String): List<AnnotatedKtResource> {
        val resources = scanResources(basePackage)
        val annotationResources = mutableListOf<AnnotatedKtResource>()
        for (resource in resources) {
            filterResource(resource)?.let {
                annotationResources.add(it)
            }
        }
        return annotationResources
    }

    fun scanResources(basePackage: String): List<KtResource> {
        return PathResourceScanner().scanByPackage(basePackage)
    }

    fun processAnnotatedResources(annotatedKtResources: List<AnnotatedKtResource>) {
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
}