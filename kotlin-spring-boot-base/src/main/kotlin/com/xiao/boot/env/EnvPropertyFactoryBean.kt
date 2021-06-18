package com.xiao.boot.env

import org.springframework.beans.factory.FactoryBean
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware

/**
 *
 * @author lix wang
 */
class EnvPropertyFactoryBean<T : Any>(
    private val clazz: Class<T>
) : FactoryBean<T>, ApplicationContextAware {
    private lateinit var applicationContext: ApplicationContext

    override fun getObject(): T {
        checkNoArgsConstructorExist(clazz)
        val instance = clazz.newInstance()
        val envInfoProvider = applicationContext.getBean(EnvInfoProvider::class.java)
        parseInstanceProperties(instance, envInfoProvider)
        return instance
    }

    override fun getObjectType(): Class<T> {
        return clazz
    }

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        this.applicationContext = applicationContext
    }

    private fun checkNoArgsConstructorExist(clazz: Class<T>) {
        if (clazz.constructors.none { it.parameterCount == 0 }) {
            throw IllegalArgumentException("Class ${clazz.name} must has a constructor without arguments.")
        }
    }

    private fun parseInstanceProperties(instance: T, envInfoProvider: EnvInfoProvider) {
        TODO()
    }
}