package com.xiao.boot.mybatis.database

import com.xiao.boot.base.env.EnvInfoProvider
import com.xiao.boot.base.env.ProfileType
import com.xiao.boot.mybatis.annotation.KtSpringDatabase
import com.xiao.boot.mybatis.testing.KtTestXMLLanguageDriver
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.apache.ibatis.session.Configuration
import org.apache.ibatis.session.LocalCacheScope
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.Resource
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import javax.sql.DataSource

/**
 *
 * @author lix wang
 */
abstract class BaseDatabase(
    private val url: String,
    private val username: String,
    private val password: String
) {
    val name: String
    private val dataScriptPattern: String
    private var testDataScriptMap: Map<String, Resource> = mapOf()
    @Volatile private var dataScriptParsed: Boolean = false

    @Autowired
    lateinit var envInfoProvider: EnvInfoProvider

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

    open fun createConfiguration(): Configuration {
        return Configuration()
            .apply {
                isMapUnderscoreToCamelCase = true
                isLazyLoadingEnabled = true
                isCacheEnabled = false
                localCacheScope = LocalCacheScope.STATEMENT
                if (envInfoProvider.profile() == ProfileType.TEST) {
                    languageRegistry.defaultDriverClass = KtTestXMLLanguageDriver::class.java
                }
                variables.setProperty(DATABASE_NAME_KEY, databaseName(name))
            }
    }

    internal fun getTestTableDataScript(table: String): Resource {
        if (!dataScriptParsed) {
            synchronized(this) {
                if (!dataScriptParsed) {
                    parseTestDataScript()
                }
            }
        }
        return testDataScriptMap[table] ?: throw IllegalStateException("Can't find data file for table: $table.")
    }

    private fun parseTestDataScript() {
        check(envInfoProvider.profile() == ProfileType.TEST) {
            "${this.name} dataScript can only be used when TEST active profile."
        }
        testDataScriptMap = PathMatchingResourcePatternResolver().getResources(dataScriptPattern)
            .associateBy {
                it.file.nameWithoutExtension
            }
    }

    companion object {
        const val DATABASE_NAME_KEY = "KT_MYSQL_DATABASE_NAME"
        fun databaseName(name: String) = "${name}Database"
        fun configurationName(name: String) = "${name}Configuration"
        fun sqlSessionFactoryName(name: String) = "${name}SqlSessionFactory"
        fun dataSourceName(name: String) = "${name}DataSource"
        fun dataSourceFactoryMethodName(): String = getDatabaseMethodName("createDataSource")
        fun configurationName(): String = getDatabaseMethodName("createConfiguration")

        private fun getDatabaseMethodName(name: String): String {
            val methods = BaseDatabase::class.java.methods
                .filter { method ->
                    method.name == name
                }
            check(methods.size == 1)
            return methods.first().name
        }
    }
}