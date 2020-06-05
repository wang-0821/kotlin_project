package com.xiao.base.context

import com.xiao.base.annotation.Component
import com.xiao.base.annotation.InitialInject
import com.xiao.base.annotation.ScannerFilter
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
            if (ScannerFilter.includeFilter(resource)) {
                handleAnnotation(resource)
            }
        }
    }

    private fun handleAnnotation(resource: KtResource) {
        for (annotation in resource.annotations()) {
            if (annotation.annotationClass == InitialInject::class) {
                resource.javaClass.newInstance()
            }
            if (annotation.annotationClass == Component::class) {
                registerSingleton(resource.javaClass, resource.javaClass.newInstance())
            }
        }
    }
}