package com.xiao.base.context

import com.xiao.base.annotation.AnnotationScan
import com.xiao.base.annotation.ContextInject
import com.xiao.base.annotation.KtComponent
import com.xiao.base.resource.AnnotatedKtResource
import com.xiao.base.resource.KtClassResource
import com.xiao.base.resource.PathResourceScanner
import com.xiao.base.util.extractAnnotations

/**
 *
 * @author lix wang
 */
object ContextScanner : BeanRegistryAware {
    fun scanAnnotatedResources(basePackage: String): List<AnnotatedKtResource> {
        val resources = PathResourceScanner.scanClassResources(basePackage)
        val annotationResources = mutableListOf<AnnotatedKtResource>()
        for (resource in resources) {
            filterResource(resource)?.let {
                annotationResources.add(it)
            }
        }
        return annotationResources
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
        val componentResources = annotatedKtResources.filter { it.isAnnotated(KtComponent::class) }
        for (resource in componentResources) {
            val component = resource.annotationsByType(KtComponent::class).first()
            val handler = component.handler.objectInstance ?: component.handler.objectInstance
            handler?.let {
                it(resource)
            }
        }
    }

    private fun filterResource(classResource: KtClassResource): AnnotatedKtResource? {
        val annotations = classResource.clazz.java.extractAnnotations()
        annotations.firstOrNull { it.annotationClass == AnnotationScan::class }?.let {
            val annotationScan = it as AnnotationScan
            val includeTypeFilter = annotationScan.includeTypeFilter.objectInstance
                ?: annotationScan.includeTypeFilter.objectInstance
            val excludeTypeFilter = annotationScan.excludeTypeFilter.objectInstance
                ?: annotationScan.excludeTypeFilter.objectInstance
            if (excludeTypeFilter != null && excludeTypeFilter(classResource)) {
                return null
            }
            if (includeTypeFilter != null && includeTypeFilter(classResource)) {
                return AnnotatedKtResource(classResource, annotations)
            }
        }
        return null
    }
}