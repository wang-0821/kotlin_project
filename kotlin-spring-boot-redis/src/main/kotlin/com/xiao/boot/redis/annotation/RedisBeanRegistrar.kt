package com.xiao.boot.redis.annotation

import com.xiao.boot.base.util.getBeanName
import com.xiao.boot.redis.client.BaseRedis.Companion.redisAsyncServiceBeanName
import com.xiao.boot.redis.client.BaseRedis.Companion.redisAsyncServiceFactoryName
import com.xiao.boot.redis.client.BaseRedis.Companion.redisClusterAsyncServiceBeanName
import com.xiao.boot.redis.client.BaseRedis.Companion.redisClusterAsyncServiceFactoryName
import com.xiao.boot.redis.client.BaseRedis.Companion.redisClusterServiceBeanName
import com.xiao.boot.redis.client.BaseRedis.Companion.redisClusterServiceFactoryName
import com.xiao.boot.redis.client.BaseRedis.Companion.redisServiceBeanName
import com.xiao.boot.redis.client.BaseRedis.Companion.redisServiceFactoryName
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.beans.factory.support.GenericBeanDefinition
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar
import org.springframework.core.annotation.AnnotationAttributes
import org.springframework.core.type.AnnotationMetadata

/**
 *
 * @author lix wang
 */
class RedisBeanRegistrar : ImportBeanDefinitionRegistrar {
    override fun registerBeanDefinitions(
        importingClassMetadata: AnnotationMetadata,
        registry: BeanDefinitionRegistry
    ) {
        AnnotationAttributes.fromMap(
            importingClassMetadata.getAnnotationAttributes(KtSpringRedis::class.java.name)
        )?.let {
            registerRedis(importingClassMetadata, it, registry)
        }
    }

    private fun registerRedis(
        importingClassMetadata: AnnotationMetadata,
        annotationAttributes: AnnotationAttributes,
        registry: BeanDefinitionRegistry
    ) {
        val beanName = registry.getBeanName(importingClassMetadata)
        val name = annotationAttributes.getString("name")
        @Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
        when (annotationAttributes.getEnum<RedisClientMode>("mode")) {
            RedisClientMode.DEFAULT -> registerRedisBean(name, beanName, registry)
            RedisClientMode.CLUSTER -> registerClusterRedisBean(name, beanName, registry)
        }
    }

    private fun registerRedisBean(name: String, beanName: String, registry: BeanDefinitionRegistry) {
        // register redis service bean
        registry.registerBeanDefinition(
            redisServiceBeanName(name),
            GenericBeanDefinition()
                .apply {
                    factoryBeanName = beanName
                    factoryMethodName = redisServiceFactoryName()
                }
        )

        // register redis async service bean
        registry.registerBeanDefinition(
            redisAsyncServiceBeanName(name),
            GenericBeanDefinition()
                .apply {
                    factoryBeanName = beanName
                    factoryMethodName = redisAsyncServiceFactoryName()
                }
        )
    }

    private fun registerClusterRedisBean(name: String, beanName: String, registry: BeanDefinitionRegistry) {
        // register cluster redis service bean
        registry.registerBeanDefinition(
            redisClusterServiceBeanName(name),
            GenericBeanDefinition()
                .apply {
                    factoryBeanName = beanName
                    factoryMethodName = redisClusterServiceFactoryName()
                }
        )

        // register cluster async service bean
        registry.registerBeanDefinition(
            redisClusterAsyncServiceBeanName(name),
            GenericBeanDefinition()
                .apply {
                    factoryBeanName = beanName
                    factoryMethodName = redisClusterAsyncServiceFactoryName()
                }
        )
    }
}