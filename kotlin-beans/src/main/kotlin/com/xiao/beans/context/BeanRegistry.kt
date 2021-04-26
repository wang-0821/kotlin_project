package com.xiao.beans.context

/**
 *
 * @author lix wang
 */
interface BeanRegistry : Context {
    fun <T : Any> getByType(clazz: Class<T>): T?

    fun <T : Any> getByName(beanName: String): T?

    fun <T : Any> register(beanName: String, bean: T)

    fun <T : Any> registerSingleton(bean: T)

    fun <T : Any> registerSingleton(name: String, bean: T)

    companion object Key : Context.Key<BeanRegistry>
}