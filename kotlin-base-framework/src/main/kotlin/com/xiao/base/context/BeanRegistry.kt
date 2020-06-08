package com.xiao.base.context

import com.xiao.base.annotation.ContextInject
import com.xiao.base.exception.HttpStatus
import com.xiao.base.exception.KtException
import java.util.concurrent.ConcurrentHashMap

/**
 *
 * @author lix wang
 */
interface BeanRegistry : Context {
    fun <T : Any> getByType(clazz: Class<T>): T?

    fun <T : Any> getByName(beanName: String): T?

    @Throws(KtException::class)
    fun <T : Any> register(beanName: String, clazz: Class<T>, bean: T)

    @Throws(KtException::class)
    fun <T : Any> registerSingleton(clazz: Class<T>, bean: T)

    companion object Key : Context.Key<BeanRegistry>
}

@ContextInject
class ContextBeanFactory : BeanRegistry, AbstractContext(BeanRegistry) {
    private val contextBeanPool = ConcurrentHashMap<String, Any>()
    private val beanNamesByType = ConcurrentHashMap<Class<*>, MutableSet<String>>()

    override fun <T : Any> getByType(clazz: Class<T>): T? {
        return beanNamesByType[clazz]?.let {
            if (it.size > 1) {
                throw KtException().statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                    .message("Duplicate bean of ${clazz.simpleName}.")
            } else {
                contextBeanPool[it.iterator().next()] as? T
            }
        } ?: null
    }

    override fun <T : Any> getByName(beanName: String): T? {
        return contextBeanPool[beanName] as? T
    }

    @Synchronized override fun <T : Any> register(beanName: String, clazz: Class<T>, bean: T) {
        beanNamesByType[clazz]?.let {
            if (!it.add(beanName)) {
                throw KtException().statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                    .message("Duplicate beanName of ${clazz.simpleName}")
            }
        } ?: kotlin.run {
            beanNamesByType[clazz] = mutableSetOf(beanName)
        }

        contextBeanPool[beanName] = bean
    }

    @Synchronized override fun <T : Any> registerSingleton(clazz: Class<T>, bean: T) {
        beanNamesByType[clazz]?.let {
            throw KtException().statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                .message("Duplicate bean of ${clazz.simpleName}")
        } ?: kotlin.run {
            val beanName = clazz.simpleName
            beanNamesByType[clazz] = mutableSetOf(beanName)
            contextBeanPool[beanName] = bean
        }
    }
}