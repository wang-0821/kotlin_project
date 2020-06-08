package com.xiao.base.annotation

import com.xiao.base.context.BeanRegistryAware
import com.xiao.base.resource.KtResource

/**
 *
 * @author lix wang
 */
typealias AnnotationHandler = (KtResource) -> Unit

object ContextInjectResourceHandler : AnnotationHandler {
    override fun invoke(p1: KtResource) {
        p1.clazz.java.newInstance()
    }
}

object ComponentResourceHandler : AnnotationHandler, BeanRegistryAware {
    override fun invoke(p1: KtResource) {
        getByType(p1.javaClass) ?: registerSingleton(p1.javaClass, p1.javaClass.newInstance())
    }
}

