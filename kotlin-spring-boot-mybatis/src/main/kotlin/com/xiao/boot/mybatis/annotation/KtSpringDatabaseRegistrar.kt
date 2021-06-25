package com.xiao.boot.mybatis.annotation

import com.xiao.base.logging.Logging
import org.mybatis.spring.mapper.ClassPathMapperScanner
import org.springframework.beans.factory.support.BeanDefinitionRegistry
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
        registry: BeanDefinitionRegistry
    ) {
        AnnotationAttributes.fromMap(
            importingClassMetadata.getAnnotationAttributes(KtSpringDatabase::class.java.name)
        )?.let {
            registerDatabase(importingClassMetadata, it, registry)
        }
    }

    private fun registerDatabase(
        annotationMetadata: AnnotationMetadata,
        annotationAttributes: AnnotationAttributes,
        registry: BeanDefinitionRegistry
    ) {
        val name = annotationAttributes.getString("name")
        val mapperBasePackage = annotationAttributes.getString("mapperBasePackage")
        val mapperXmlPattern = annotationAttributes.getString("mapperXmlPattern")
        val dataScriptPattern = annotationAttributes.getString("dataScriptPattern")
        check(!name.isNullOrEmpty() && !mapperBasePackage.isNullOrEmpty())
        val mapperBasePackages = StringUtils.tokenizeToStringArray(
            mapperBasePackage,
            ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS
        )

        val scanner = ClassPathMapperScanner(registry)
        scanner.registerFilters()
        val beanDefinitionHolders = scanner.doScan(mapperBasePackage)
        val xmlMapperResources = (scanner.resourceLoader as ResourcePatternResolver).getResources(mapperXmlPattern)
        if (beanDefinitionHolders.isNullOrEmpty() && xmlMapperResources.isNullOrEmpty()) {
            if (log.isInfoEnabled) {
                log.info("No mapper found for database $name.")
            }
            return
        }


    }

    companion object : Logging()
}