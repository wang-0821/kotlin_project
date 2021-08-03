package com.xiao.boot.base.util

import com.xiao.boot.base.env.ProfileType
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.boot.web.context.WebServerApplicationContext
import org.springframework.core.env.Environment
import org.springframework.util.ClassUtils
import org.springframework.util.StringUtils

/**
 *
 * @author lix wang
 */
fun AnnotatedBeanDefinition.className(): String {
    return factoryMethodMetadata?.returnTypeName ?: metadata.className
}

fun Environment.activeProfileType(): ProfileType {
    val profiles = this.activeProfiles
        .mapNotNullTo(HashSet()) {
            ProfileType.match(it)
        }
    check(profiles.size == 1) {
        "Environment must have only 1 activate profile, " +
            "current found ${profiles.size}."
    }
    return profiles.first()
}

fun WebServerApplicationContext.serverName(): String {
    return if (StringUtils.hasText(this.serverNamespace)) {
        this.serverNamespace
    } else {
        "server"
    }
}

fun BeanDefinitionRegistry.getBeanDifinitionsByFilter(
    filter: (BeanDefinition) -> Boolean
): Map<String, BeanDefinition> {
    val result = mutableMapOf<String, BeanDefinition>()
    beanDefinitionNames
        .forEach { beanName ->
            val beanDifinition = getBeanDefinition(beanName)
            if (filter(beanDifinition)) {
                result[beanName] = beanDifinition
            }
        }
    return result
}

fun BeanDefinitionRegistry.getBeanDefinitionsByBeanClassName(beanClassName: String): Map<String, BeanDefinition> {
    return getBeanDifinitionsByFilter { beanDefinition ->
        beanDefinition.beanClassName == beanClassName
    }
}

fun BeanDefinitionRegistry.getBeanDefinitionsByType(type: Class<*>): Map<String, BeanDefinition> {
    return getBeanDifinitionsByFilter { beanDifinition ->
        if (beanDifinition is AnnotatedBeanDefinition) {
            val clazz = ClassUtils.forName(beanDifinition.className(), null)
            if (type.isAssignableFrom(clazz)) {
                return@getBeanDifinitionsByFilter true
            }
        }
        return@getBeanDifinitionsByFilter false
    }
}

fun BeanDefinitionRegistry.doByBeanClassFilter(
    filter: (Class<*>) -> Boolean,
    exec: (String, Class<*>, BeanDefinition) -> Unit
) {
    beanDefinitionNames
        .forEach { beanName ->
            val beanDefinition = getBeanDefinition(beanName)
            if (beanDefinition is AnnotatedBeanDefinition) {
                val className = beanDefinition.className()
                val clazz = ClassUtils.forName(className, null)
                if (filter(clazz)) {
                    exec(beanName, clazz, beanDefinition)
                }
            }
        }
}