package com.xiao.boot.mybatis.database

import com.xiao.boot.mybatis.annotation.KtSpringDatabase
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.joda.time.DateTimeZone
import javax.sql.DataSource

/**
 *
 * @author lix wang
 */
abstract class BaseDatabase(
    private val url: String,
    private val username: String,
    private val password: String,
    private val timeZone: DateTimeZone = DateTimeZone.UTC
) {
    private val name: String
    private val dataScriptPattern: String

    init {
        javaClass.getAnnotation(KtSpringDatabase::class.java)
            .let {
                name = it.name
                dataScriptPattern = it.dataScriptPattern
            }
    }

    open fun createDataSource(): DataSource {
        return HikariDataSource(
            HikariConfig()
                .apply {
                    username = this@BaseDatabase.username
                    password = this@BaseDatabase.password
                    jdbcUrl = this@BaseDatabase.url
                    maximumPoolSize = 32
                    minimumIdle = 1
                    connectionTimeout = 5000
                    idleTimeout = 60000
                    initializationFailTimeout = 0
                }
        )
    }

    companion object {
        fun sqlSessionFactoryName(databaseName: String) = "${databaseName}SqlSessionFactory"
        fun dataSourceName(databaseName: String) = "${databaseName}DataSource"
        fun dataSourceFactoryMethodName(): String {
            val methods = BaseDatabase::class.java.methods
                .filter { method ->
                    method.name == "createDataSource"
                }
            check(methods.size == 1)
            return methods.first().name
        }
    }
}