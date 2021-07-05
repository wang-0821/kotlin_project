package com.xiao.boot.base.filter

import org.springframework.boot.autoconfigure.AutoConfigurationImportFilter
import org.springframework.boot.autoconfigure.AutoConfigurationMetadata

class AutoConfigurationExcludeFilter : AutoConfigurationImportFilter {
    override fun match(
        autoConfigurationClasses: Array<out String>,
        autoConfigurationMetadata: AutoConfigurationMetadata
    ): BooleanArray {
        val result = BooleanArray(autoConfigurationClasses.size) { true }
        for (i in autoConfigurationClasses.indices) {
            if (excludeAutoConfigurations.contains(autoConfigurationClasses[i])) {
                result[i] = false
            }
        }
        return result
    }

    companion object {
        private val excludeAutoConfigurations = listOf(
            "org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration",
            "org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration",
            "org.springframework.boot.autoconfigure.dao.PersistenceExceptionTranslationAutoConfiguration",
            "org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration",
            "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration",
            "org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration",
            "org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration",
            "org.springframework.boot.autoconfigure.netty.NettyAutoConfiguration",
            "org.springframework.boot.autoconfigure.sql.init.SqlInitializationAutoConfiguration",
            "org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration",
            "org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration"
        )
    }
}