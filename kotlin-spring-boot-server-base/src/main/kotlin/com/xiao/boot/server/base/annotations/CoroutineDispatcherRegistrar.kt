package com.xiao.boot.server.base.annotations

import com.xiao.boot.base.thread.KtThreadPool
import com.xiao.boot.base.util.getBeanDefinitionsByType
import com.xiao.boot.server.base.servlet.CoroutineServerArgs
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor
import org.springframework.beans.factory.support.GenericBeanDefinition
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware

/**
 * Allow servlet web application use coroutine.
 *
 * @author lix wang
 */
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
class CoroutineDispatcherRegistrar : BeanDefinitionRegistryPostProcessor, ApplicationContextAware {
    private lateinit var applicationContext: ApplicationContext

    override fun postProcessBeanFactory(beanFactory: ConfigurableListableBeanFactory) {
        // do nothing
    }

    override fun postProcessBeanDefinitionRegistry(registry: BeanDefinitionRegistry) {
        synchronized(CoroutineDispatcherRegistrar::class.java) {
            val coroutineServerArgsBeans = registry.getBeanDefinitionsByType(CoroutineServerArgs::class.java)
            if (coroutineServerArgsBeans.values.size > 1) {
                throw IllegalStateException("Duplicate bean type: ${CoroutineServerArgs::javaClass.name}.")
            }
            if (coroutineServerArgsBeans.isEmpty()) {
                val beanDefinition = GenericBeanDefinition()
                    .apply {
                        beanClass = CoroutineServerArgs::class.java
                        isPrimary = true
                        propertyValues.add("enableGlobalDispatcher", true)
                        propertyValues.add("coroutineScope", KtThreadPool.globalCoroutineScope)
                        propertyValues.add("executorService", KtThreadPool.globalPool)
                    }
                registry.registerBeanDefinition(CoroutineServerArgs::javaClass.name, beanDefinition)
            }
        }
    }

    override fun setApplicationContext(context: ApplicationContext) {
        this.applicationContext = context
    }
}