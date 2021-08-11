package xiao.beans.annotation

import xiao.beans.context.BeanHelper
import xiao.beans.context.BeanRegistryAware
import xiao.beans.context.Context
import xiao.beans.resource.AnnotatedKtResource

/**
 *
 * @author lix wang
 */
typealias AnnotationHandler = (AnnotatedKtResource) -> Unit

object ContextInjectResourceHandler : AnnotationHandler {
    override fun invoke(p1: AnnotatedKtResource) {
        val obj = BeanHelper.newInstance<Context>(p1.classResource.clazz.java)
        obj.register(obj.key)
    }
}

object ComponentResourceHandler : AnnotationHandler, BeanRegistryAware {
    override fun invoke(p1: AnnotatedKtResource) {
        val component = p1.annotationsByType(KtComponent::class).first()
        val obj = BeanHelper.newInstance<Any>(p1.classResource.clazz.java)
        getByType(p1.classResource.clazz.java) ?: kotlin.run {
            if (component.value.isBlank()) {
                registerSingleton(obj)
            } else {
                registerSingleton(component.value, obj)
            }
        }
    }
}