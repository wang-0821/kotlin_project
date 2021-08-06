package com.xiao.boot.server.base.annotations

import com.xiao.boot.base.thread.KtThreadPool
import com.xiao.boot.server.base.mvc.KtServerArgs
import com.xiao.boot.server.base.mvc.KtWebMvcRegistrations
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.beans.factory.config.ConstructorArgumentValues
import org.springframework.beans.factory.config.RuntimeBeanReference
import org.springframework.beans.factory.support.AbstractBeanDefinition
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor
import org.springframework.beans.factory.support.GenericBeanDefinition
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.core.Ordered
import util.getBeanDefinitionsByType

/**
 * Allow servlet web application use coroutine.
 *
 * @author lix wang
 */
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
class CoroutineDispatcherRegistrar : BeanDefinitionRegistryPostProcessor, Ordered {
    override fun postProcessBeanFactory(beanFactory: ConfigurableListableBeanFactory) {
        // do nothing
    }

    override fun postProcessBeanDefinitionRegistry(registry: BeanDefinitionRegistry) {
        val serverArgsMap = registry.getBeanDefinitionsByType(KtServerArgs::class.java)
        if (serverArgsMap.entries.size > 1) {
            throw IllegalStateException(
                "Duplicate ${KtServerArgs::class.java.name} Beans: " +
                    "${serverArgsMap.keys.joinToString()}."
            )
        }
        val serverArgsBeanName = serverArgsMap.entries.first().key
        val serverArgsBeanDefinition = serverArgsMap.entries.first().value as AbstractBeanDefinition
        updateServerArgsAttributes(serverArgsBeanDefinition)
        registerCustomWebMvcRegistrationsBean(serverArgsBeanName, registry)
    }

    private fun updateServerArgsAttributes(
        beanDefinition: AbstractBeanDefinition
    ) {
        beanDefinition.apply {
            setBeanDefinitionProperty(this, "enableCoroutineDispatcher", true)
            setBeanDefinitionProperty(this, "coroutineScope", KtThreadPool.globalCoroutineScope)
            setBeanDefinitionProperty(this, "executorService", KtThreadPool.globalPool)
        }
    }

    private fun setBeanDefinitionProperty(beanDefinition: AbstractBeanDefinition, propertyName: String, value: Any?) {
        if (beanDefinition.propertyValues.contains(propertyName)) {
            beanDefinition.propertyValues.removePropertyValue(propertyName)
        }
        beanDefinition.propertyValues.add(propertyName, value)
    }

    private fun registerCustomWebMvcRegistrationsBean(
        serverArgsBeanName: String,
        registry: BeanDefinitionRegistry
    ) {
        registry.registerBeanDefinition(
            KtWebMvcRegistrations::class.java.name,
            GenericBeanDefinition()
                .apply {
                    isPrimary = true
                    beanClass = KtWebMvcRegistrations::class.java
                    constructorArgumentValues = ConstructorArgumentValues()
                        .apply {
                            addGenericArgumentValue(RuntimeBeanReference(serverArgsBeanName))
                        }
                }
        )
    }

    override fun getOrder(): Int {
        return Ordered.LOWEST_PRECEDENCE
    }
}