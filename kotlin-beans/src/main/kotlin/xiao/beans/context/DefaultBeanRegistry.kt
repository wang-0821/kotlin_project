package xiao.beans.context

import xiao.beans.annotation.ContextInject
import java.util.concurrent.ConcurrentHashMap

/**
 *
 * @author lix wang
 */
@ContextInject
@Suppress("UNCHECKED_CAST")
class DefaultBeanRegistry : BeanRegistry {
    override val key: Context.Key<*>
        get() = BeanRegistry
    private val contextBeanPool = ConcurrentHashMap<String, Any>()
    private val beanNamesByType = mutableMapOf<Class<*>, MutableSet<String>>()

    override fun <T : Any> getByType(clazz: Class<T>): T? {
        val beanNames = beanNamesByType.entries.filter { clazz.isAssignableFrom(it.key) }.flatMap { it.value }
        check(beanNames.size <= 1) {
            "Duplicate bean of ${clazz.simpleName}."
        }
        return if (beanNames.isNotEmpty()) {
            contextBeanPool[beanNames[0]] as T?
        } else {
            null
        }
    }

    override fun <T : Any> getByName(beanName: String): T? {
        return contextBeanPool[beanName] as? T
    }

    override fun <T : Any> register(beanName: String, bean: T) {
        synchronized(contextBeanPool) {
            beanNamesByType[bean::class.java]?.let {
                check(it.add(beanName)) {
                    "Duplicate beanName of $beanName"
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