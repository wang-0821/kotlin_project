package com.xiao.base.context

import com.xiao.base.annotation.ContextInject
import java.util.concurrent.ConcurrentHashMap

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

@ContextInject
@Suppress("UNCHECKED_CAST")
class ContextBeanFactory : BeanRegistry, AbstractContext(BeanRegistry) {
    private val contextBeanPool = ConcurrentHashMap<String, Any>()
    private val beanNamesByType = mutableMapOf<Class<*>, MutableSet<String>>()

    override fun <T : Any> getByType(clazz: Class<T>): T? {
        return beanNamesByType[clazz]?.let {
            if (it.size > 1) {
                throw IllegalStateException("Duplicate bean of ${clazz.simpleName}.")
            } else {
                contextBeanPool[it.iterator().next()] as? T
            }
        }
    }

    override fun <T : Any> getByName(beanName: String): T? {
        return contextBeanPool[beanName] as? T
    }

    override fun <T : Any> register(beanName: String, bean: T) {
        synchronized(contextBeanPool) {
            beanNamesByType[bean::class.java]?.let {
                if (!it.add(beanName)) {
                    throw IllegalStateException("Duplicate beanName of $beanName")
                }
            } ?: kotlin.run {
                beanNamesByType[bean::class.java] = mutableSetOf(beanName)
            }

            contextBeanPool[beanName] = bean
        }
    }

    override fun <T : Any> registerSingleton(bean: T) {
        register(bean::class.java.simpleName, bean)
    }

    override fun <T : Any> registerSingleton(name: String, bean: T) {
        synchronized(contextBeanPool) {
            beanNamesByType[bean::class.java]?.let {
                throw IllegalStateException("Duplicate bean of ${bean::class.java.simpleName}")
            } ?: kotlin.run {
                beanNamesByType[bean::class.java] = mutableSetOf(name)
                contextBeanPool[name] = bean
            }
        }
    }
}