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

        val databaseBeanName = getDatabaseBeanName(importingClassMetadata, registry)
        val scanner = ClassPathMapperScanner(registry)

        // register mapper beanDefinition list
        registerMapperBeanDefinitions(name, mapperBasePackages, scanner)

        // register dataSource beanDefinition
        val dataSourceBeanName = dataSourceName(name)
        registerSourceBeanDefinition(databaseBeanName, dataSourceBeanName, registry)

        // register sqlSessionFactory beanDefinition
        registerSqlSessionFactoryBeanDefinition(
            sqlSessionFactoryName(name),
            dataSourceBeanName,
            mapperXmlPattern,
            scanner.resourceLoader as ResourcePatternResolver,
            registry
        )
    }

    private fun getDatabaseBeanName(
        importingClassMetadata: AnnotationMetadata,
        registry: BeanDefinitionRegistry
    ): String {
        val beanClassName = importingClassMetadata.className
        val beanNames = registry.beanDefinitionNames
            .filter { beanDefinitionName ->
                registry.getBeanDefinition(beanDefinitionName).beanClassName == beanClassName
            }
        check(beanNames.size == 1)
        return beanNames.first()
    }

    private fun registerMapperBeanDefinitions(
        databaseName: String,
        mapperBasePackages: Array<String>,
        scanner: ClassPathMapperScanner,
    ) {
        scanner
            .apply {
                setMapperFactoryBeanClass(KtMapperFactoryBean::class.java)
                setSqlSessionFactoryBeanName(sqlSessionFactoryName(databaseName))
                registerFilters()
                doScan(*mapperBasePackages)
            }
    }

    private fun registerSqlSessionFactoryBeanDefinition(
        beanName: String,
        dataSourceBeanName: String,
        mapperXmlPattern: String,
        scanner: ResourcePatternResolver,
        registry: BeanDefinitionRegistry
    ) {
        val xmlMapperResources = scanner.getResources(mapperXmlPattern)
        registry.registerBeanDefinition(
            beanName,
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

    private fun registerSourceBeanDefinition(
        databaseBeanName: String,
        dataSourceBeanName: String,
        registry: BeanDefinitionRegistry
    ) {
        registry.registerBeanDefinition(
            dataSourceBeanName,
            GenericBeanDefinition()
                .apply {
                    factoryBeanName = databaseBeanName
                    factoryMethodName = dataSourceFactoryMethodName()
                }
        )
    }
}