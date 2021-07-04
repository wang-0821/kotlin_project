package com.xiao.boot.base

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration
import org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration
import org.springframework.boot.autoconfigure.dao.PersistenceExceptionTranslationAutoConfiguration
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration
import org.springframework.boot.autoconfigure.netty.NettyAutoConfiguration
import org.springframework.boot.autoconfigure.sql.init.SqlInitializationAutoConfiguration
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration

@EnableAutoConfiguration(
    exclude = [
        CacheAutoConfiguration::class,
        MessageSourceAutoConfiguration::class,
        PersistenceExceptionTranslationAutoConfiguration::class,
        JacksonAutoConfiguration::class,
        DataSourceAutoConfiguration::class,
        JdbcTemplateAutoConfiguration::class,
        DataSourceTransactionManagerAutoConfiguration::class,
        NettyAutoConfiguration::class,
        SqlInitializationAutoConfiguration::class,
        TransactionAutoConfiguration::class
    ]
)
abstract class BaseEnableAutoConfiguration