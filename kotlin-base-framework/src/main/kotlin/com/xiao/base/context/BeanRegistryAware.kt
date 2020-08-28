package com.xiao.base.context

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

    fun <T : Any> registerSingleton(bean: T) {
        get(BeanRegistry.Key)?.registerSingleton(bean)
    }

    fun <T : Any> registerSingleton(name: String, bean: T) {
        get(BeanRegistry.Key)?.registerSingleton(name, bean)
    }

    override val key: Context.Key<*>
        get() = BeanRegistry.Key
}