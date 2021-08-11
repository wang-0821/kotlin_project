package xiao.boot.base.property

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.beans.factory.config.ConstructorArgumentValues
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor
import org.springframework.beans.factory.support.GenericBeanDefinition
import org.springframework.stereotype.Component
import xiao.base.util.doByBeanClassFilter

/**
 *
 * @author lix wang
 */
@Component
class KtConfigurationPostProcessor : BeanDefinitionRegistryPostProcessor {
    override fun postProcessBeanDefinitionRegistry(registry: BeanDefinitionRegistry) {
        registry.doByBeanClassFilter(
            { clazz -> clazz.isAnnotationPresent(KtConfiguration::class.java) }
        ) { beanName, clazz, _ ->
            postProcessKtConfiguration(beanName, clazz, registry)
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