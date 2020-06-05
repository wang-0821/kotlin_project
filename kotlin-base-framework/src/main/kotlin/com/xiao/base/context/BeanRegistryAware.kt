package com.xiao.base.context

import com.xiao.base.exception.KtException

/**
 *
 * @author lix wang
 */
interface BeanRegistryAware : Context {
    fun <T : Any> getByName(beanName: String): T? {
        return Context.get(BeanRegistry.Key)?.getByName(beanName)
    }

    fun <T : Any> getByType(clazz : Class<T>): T? {
        return Context.get(BeanRegistry.Key)?.getByType(clazz)
    }

    @Throws(KtException::class)
    fun <T : Any> registerSingleton(clazz: Class<T>, bean: T) {
        Context.get(BeanRegistry.Key)?.registerSingleton(clazz, bean)
    }

    override val key: Context.Key<*>
        get() = BeanRegistry.Key
}