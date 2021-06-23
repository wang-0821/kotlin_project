package com.xiao.boot.base.env

import com.xiao.boot.base.util.className
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.beans.factory.config.ConstructorArgumentValues
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor
import org.springframework.beans.factory.support.GenericBeanDefinition
import org.springframework.stereotype.Component
import org.springframework.util.ClassUtils

/**
 *
 * @author lix wang
 */
@Component
class KtConfigurationPostProcessor : BeanDefinitionRegistryPostProcessor {
    override fun postProcessBeanDefinitionRegistry(registry: BeanDefinitionRegistry) {
        registry.beanDefinitionNames.forEach { beanName ->
            val beanDefinition = registry.getBeanDefinition(beanName)
            if (beanDefinition is AnnotatedBeanDefinition) {
                val className = beanDefinition.className()
                val clazz = ClassUtils.forName(className, null)
                if (clazz.isAnnotationPresent(KtConfiguration::class.java)) {
                    postProcessKtConfiguration(beanName, clazz, registry)
                }
            }
        }
    }

    override fun postProcessBeanFactory(beanFactory: ConfigurableListableBeanFactory) {
        // execute after postProcessBeanDefinitionRegistry(registry)
    }

    private fun postProcessKtConfiguration(
        beanName: String,
        clazz: Class<*>,
        registry: BeanDefinitionRegistry
    ) {
        val newBeanDefinition = GenericBeanDefinition()
            .apply {
                isPrimary = true
                beanClass = EnvPropertyFactoryBean::class.java
                constructorArgumentValues = ConstructorArgumentValues()
                    .apply {
                        addGenericArgumentValue(clazz)
                    }
            }
        registry.removeBeanDefinition(beanName)
        registry.registerBeanDefinition(beanName, newBeanDefinition)
    }
}