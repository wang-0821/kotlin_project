package com.xiao.base.annotation

import com.xiao.base.context.BeanRegistryAware

/**
 *
 * @author lix wang
 */
typealias AnnotationHandler = (AnnotatedKtResource) -> Unit

object ContextInjectResourceHandler : AnnotationHandler {
    override fun invoke(p1: AnnotatedKtResource) {
        p1.resource.clazz.java.newInstance()
    }
}

object ComponentResourceHandler : AnnotationHandler, BeanRegistryAware {
    override fun invoke(p1: AnnotatedKtResource) {
        val component = p1.annotationsByType(Component::class).first()
        getByType(p1.resource.clazz::class.java) ?: kotlin.run {
            if (component.value.isBlank()) {
                registerSingleton(p1.resource.clazz::class.java.newInstance())
            } else {
                registerSingleton(component.value, p1.resource.clazz::class.java.newInstance())
            }
        }
    }
}

