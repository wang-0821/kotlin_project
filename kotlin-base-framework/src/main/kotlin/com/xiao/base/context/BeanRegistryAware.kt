package com.xiao.base.context

import com.xiao.base.exception.KtException

/**
 *
 * @author lix wang
 */
interface BeanRegistryAware : ContextAware {
    fun <T : Any> getByName(beanName: String): T? {
        return get(BeanRegistry.Key)?.getByName(beanName)
    }

    fun <T : Any> getByType(clazz : Class<T>): T? {
        return get(BeanRegistry.Key)?.getByType(clazz)
    }

    @Throws(KtException::class)
    fun <T : Any> registerSingleton(clazz: Class<T>, bean: T) {
        get(BeanRegistry.Key)?.registerSingleton(clazz, bean)
    }

    override val key: Context.Key<*>
        get() = BeanRegistry.Key
}