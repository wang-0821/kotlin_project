package xiao.beans.context

/**
 *
 * @author lix wang
 */
interface BeanRegistryAware : ContextAware {
    fun <T : Any> getByName(beanName: String): T? {
        return get(BeanRegistry)?.getByName(beanName)
    }

    fun <T : Any> getByType(clazz: Class<T>): T? {
        return get(BeanRegistry)?.getByType(clazz)
    }

    fun <T : Any> registerSingleton(bean: T) {
        get(BeanRegistry)?.registerSingleton(bean)
    }

    fun <T : Any> registerSingleton(name: String, bean: T) {
        get(BeanRegistry)?.registerSingleton(name, bean)
    }
}