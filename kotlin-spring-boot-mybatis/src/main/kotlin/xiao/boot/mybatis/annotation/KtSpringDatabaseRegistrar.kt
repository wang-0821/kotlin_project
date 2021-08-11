package xiao.boot.mybatis.annotation

import org.mybatis.spring.SqlSessionFactoryBean
import org.mybatis.spring.mapper.ClassPathMapperScanner
import org.springframework.beans.factory.config.ConstructorArgumentValues
import org.springframework.beans.factory.config.RuntimeBeanReference
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.beans.factory.support.GenericBeanDefinition
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar
import org.springframework.core.annotation.AnnotationAttributes
import org.springframework.core.io.support.ResourcePatternResolver
import org.springframework.core.type.AnnotationMetadata
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.util.StringUtils
import xiao.base.util.getBeanDefinitionsByBeanClassName
import xiao.boot.mybatis.database.BaseDatabase.Companion.configurationFactoryMethodName
import xiao.boot.mybatis.database.BaseDatabase.Companion.dataSourceFactoryMethodName
import xiao.boot.mybatis.database.BaseDatabase.Companion.dataSourceName
import xiao.boot.mybatis.database.BaseDatabase.Companion.sqlSessionFactoryName
import xiao.boot.mybatis.database.BaseDatabase.Companion.transactionManagerName
import xiao.boot.mybatis.database.BaseDatabase.Companion.transactionServiceName
import xiao.boot.mybatis.factory.KtMapperFactoryBean
import xiao.boot.mybatis.tx.SpringPlatformTransactionService

/**
 *
 * @author lix wang
 */
class KtSpringDatabaseRegistrar : ImportBeanDefinitionRegistrar {
    override fun registerBeanDefinitions(
        importingClassMetadata: AnnotationMetadata,
        registry: BeanDefinitionRegistry
    ) {
        AnnotationAttributes.fromMap(
            importingClassMetadata.getAnnotationAttributes(xiao.boot.mybatis.annotation.KtSpringDatabase::class.java.name)
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
        val dataSourceBeanName = dataSourceName(name)
        val sqlSessionFactoryBeanName = sqlSessionFactoryName(name)
        val configurationBeanName = configurationFactoryMethodName(name)
        val transactionManagerBeanName = transactionManagerName(name)

        // register mapper beanDefinition list
        registerMapperBeanDefinitions(mapperBasePackages, sqlSessionFactoryBeanName, scanner)

        // register dataSource beanDefinition
        registerDataSourceBeanDefinition(databaseBeanName, dataSourceBeanName, registry)

        // register configuration beanDefinition
        registerConfigurationBeanDefinition(databaseBeanName, configurationBeanName, registry)

        // register transaction manager for spring tx
        registerTransactionManager(transactionManagerBeanName, dataSourceBeanName, registry)

        // register transaction service beanDefinition
        registerTransactionService(transactionServiceName(name), transactionManagerBeanName, registry)

        // register sqlSessionFactory beanDefinition
        registerSqlSessionFactoryBeanDefinition(
            sqlSessionFactoryBeanName,
            dataSourceBeanName,
            configurationBeanName,
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
        val beanNames = registry.getBeanDefinitionsByBeanClassName(beanClassName).keys
        check(beanNames.size == 1)
        return beanNames.first()
    }

    private fun registerMapperBeanDefinitions(
        mapperBasePackages: Array<String>,
        sqlSessionFactoryBeanName: String,
        scanner: ClassPathMapperScanner,
    ) {
        scanner
            .apply {
                setMapperFactoryBeanClass(KtMapperFactoryBean::class.java)
                setSqlSessionFactoryBeanName(sqlSessionFactoryBeanName)
                registerFilters()
                doScan(*mapperBasePackages)
            }
    }

    private fun registerDataSourceBeanDefinition(
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

    private fun registerConfigurationBeanDefinition(
        databaseBeanName: String,
        configurationBeanName: String,
        registry: BeanDefinitionRegistry
    ) {
        registry.registerBeanDefinition(
            configurationBeanName,
            GenericBeanDefinition()
                .apply {
                    factoryBeanName = databaseBeanName
                    factoryMethodName = configurationFactoryMethodName()
                }
        )
    }

    private fun registerSqlSessionFactoryBeanDefinition(
        beanName: String,
        dataSourceBeanName: String,
        configurationBeanName: String,
        mapperXmlPattern: String,
        scanner: ResourcePatternResolver,
        registry: BeanDefinitionRegistry
    ) {
        registry.registerBeanDefinition(
            beanName,
            GenericBeanDefinition()
                .apply {
                    beanClass = SqlSessionFactoryBean::class.java
                    propertyValues.add("dataSource", RuntimeBeanReference(dataSourceBeanName))
                    propertyValues.add("configuration", RuntimeBeanReference(configurationBeanName))
                    if (mapperXmlPattern.isNotEmpty()) {
                        val xmlMapperResources = scanner.getResources(mapperXmlPattern)
                        propertyValues.add("mapperLocations", xmlMapperResources)
                    }
                }
        )
    }

    private fun registerTransactionManager(
        beanName: String,
        dataSourceBeanName: String,
        registry: BeanDefinitionRegistry
    ) {
        registry.registerBeanDefinition(
            beanName,
            GenericBeanDefinition()
                .apply {
                    beanClass = DataSourceTransactionManager::class.java
                    constructorArgumentValues = ConstructorArgumentValues()
                        .apply {
                            addGenericArgumentValue(RuntimeBeanReference(dataSourceBeanName))
                        }
                }
        )
    }

    private fun registerTransactionService(
        beanName: String,
        transactionManagerBeanName: String,
        registry: BeanDefinitionRegistry
    ) {
        registry.registerBeanDefinition(
            beanName,
            GenericBeanDefinition()
                .apply {
                    beanClass = SpringPlatformTransactionService::class.java
                    constructorArgumentValues = ConstructorArgumentValues()
                        .apply {
                            addGenericArgumentValue(RuntimeBeanReference(transactionManagerBeanName))
                        }
                }
        )
    }
}