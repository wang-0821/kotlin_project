package com.xiao.boot.server.base.annotations

import com.xiao.boot.server.base.bean.CoroutineServerArgs
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor
import org.springframework.beans.factory.support.BeanNameGenerator
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


        val beanDefinition = GenericBeanDefinition()
            .apply {
                beanClass = CoroutineServerArgs::class.java
                isPrimary = true
            }
        val beanName = applicationContext.getBean(BeanNameGenerator::class.java)
            ?.let {
                it.generateBeanName(beanDefinition, registry)
            } ?: beanDefinition.beanClass.name
    }

    override fun setApplicationContext(context: ApplicationContext) {
        this.applicationContext = context
    }
}