package com.xiao.boot.mybatis.annotation

import com.xiao.boot.mybatis.database.BaseDatabase.Companion.dataSourceFactoryMethodName
import com.xiao.boot.mybatis.database.BaseDatabase.Companion.dataSourceName
import com.xiao.boot.mybatis.database.BaseDatabase.Companion.sqlSessionFactoryName
import com.xiao.boot.mybatis.factory.KtMapperFactoryBean
import com.xiao.boot.mybatis.factory.KtSqlSessionFactoryBean
import org.mybatis.spring.mapper.ClassPathMapperScanner
import org.springframework.beans.factory.config.ConstructorArgumentValues
import org.springframework.beans.factory.config.RuntimeBeanReference
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.beans.factory.support.BeanNameGenerator
import org.springframework.beans.factory.support.GenericBeanDefinition
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar
import org.springframework.core.annotation.AnnotationAttributes
import org.springframework.core.io.support.ResourcePatternResolver
import org.springframework.core.type.AnnotationMetadata
import org.springframework.util.StringUtils

/**
 *
 * @author lix wang
 */
class KtSpringDatabaseRegistrar : ImportBeanDefinitionRegistrar {
    override fun registerBeanDefinitions(
        importingClassMetadata: AnnotationMetadata,
        registry: BeanDefinitionRegistry,
        importBeanNameGenerator: BeanNameGenerator
    ) {
        AnnotationAttributes.fromMap(
            importingClassMetadata.getAnnotationAttributes(KtSpringDatabase::class.java.name)
        )?.let {
            registerDatabase(importingClassMetadata, it, registry)
        }
    }

    private fun registerDatabase(
        importingClassMetadata: AnnotationMetadata,
        annotationAttributes: AnnotationAttributes,
        registry: BeanDefinitionRegistry
    ) {
        val name = annotationAttributes.getString("name")
        val mapperBasePackage = annotationAttributes.getString("mapperBasePackage")
        val mapperXmlPattern = annotationAttributes.getString("mapperXmlPattern")
        check(!name.isNullOrEmpty() && !mapperBasePackage.isNullOrEmpty())
        val mapperBasePackages = StringUtils.tokenizeToStringArray(
            mapperBasePackage,
            ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS
        )

        // register database bean
        val beanClassName = importingClassMetadata.className
        val beanNames = registry.beanDefinitionNames
            .filter { beanDefinitionName ->
                registry.getBeanDefinition(beanDefinitionName).beanClassName == beanClassName
            }
        check(beanNames.size == 1)
        val databaseBeanName = beanNames.first()

        // register mapper beanDefinition
        val scanner = ClassPathMapperScanner(registry)
            .apply {
                setMapperFactoryBeanClass(KtMapperFactoryBean::class.java)
                setSqlSessionFactoryBeanName(sqlSessionFactoryName(name))
                registerFilters()
                doScan(*mapperBasePackages)
            }
        val xmlMapperResources = (scanner.resourceLoader as ResourcePatternResolver).getResources(mapperXmlPattern)

        // register dataSource beanDefinition
        val dataSourceBeanName = dataSourceName(name)
        registry.registerBeanDefinition(
            dataSourceBeanName,
            GenericBeanDefinition()
                .apply {
                    factoryBeanName = databaseBeanName
                    factoryMethodName = dataSourceFactoryMethodName()
                }
        )

        // register sqlSessionFactory beanDefinition
        registry.registerBeanDefinition(
            sqlSessionFactoryName(name),
            GenericBeanDefinition()
                .apply {
                    beanClass = KtSqlSessionFactoryBean::class.java
                    constructorArgumentValues = ConstructorArgumentValues()
                        .apply {
                            addGenericArgumentValue(xmlMapperResources)
                        }
                    propertyValues.add("dataSource", RuntimeBeanReference(dataSourceBeanName))
                }
        )
    }
}