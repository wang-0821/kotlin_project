package com.xiao.databse

import com.xiao.beans.resource.PathResourceScanner
import com.xiao.databse.annotation.KtDatabase
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.apache.ibatis.builder.xml.XMLMapperBuilder
import org.apache.ibatis.mapping.Environment
import org.apache.ibatis.session.Configuration
import org.apache.ibatis.session.LocalCacheScope
import org.apache.ibatis.session.SqlSessionFactory
import java.nio.file.Files
import javax.sql.DataSource

/**
 *
 * @author lix wang
 */
abstract class BaseDatabase(
    val url: String,
    val username: String,
    val password: String
) {
    @Volatile
    private var sqlSessionFactory: SqlSessionFactory? = null
    private val config: KtDatabase

    init {
        val annotationName = KtDatabase::class.simpleName
        val config = javaClass.getAnnotation(KtDatabase::class.java)
            ?: throw IllegalArgumentException("${javaClass.simpleName} must annotated by $annotationName")

        if (config.mapperBasePackages.isEmpty() && config.mapperXmlLocation.isEmpty()) {
            throw IllegalArgumentException("$annotationName mapperPath and mapperXmlPath can't be both empty.")
        }
        this.config = config
    }

    fun datasetPath(): String {
        return config.dataSetLocation
    }

    fun name(): String {
        return config.name + DATABASE_SUFFIX
    }

    fun sqlSessionFactory(): SqlSessionFactory {
        if (sqlSessionFactory != null) {
            return sqlSessionFactory!!
        } else {
            synchronized(this) {
                if (sqlSessionFactory == null) {
                    sqlSessionFactory = createSqlSessionFactory()
                }
            }
        }
        return sqlSessionFactory!!
    }

    fun dataSource(): DataSource {
        return sqlSessionFactory().configuration.environment.dataSource
    }

    private fun createSqlSessionFactory(): SqlSessionFactory {
        val configuration = createConfiguration()
        // scan mappers
        scanXmlMappers(configuration, config.mapperXmlLocation)
        scanInterfaceMappers(configuration, config.mapperBasePackages)

        setEnvironment(config.name + ENVIRONMENT_NAME_SUFFIX, configuration, url, username, password)
        return KtManagedSqlSessionFactory(configuration)
    }

    private fun scanXmlMappers(configuration: Configuration, mapperXmlPath: String) {
        if (mapperXmlPath.isBlank()) {
            return
        }

        val path = if (mapperXmlPath.startsWith(CLASSPATH_INCLUDE_JAR_PREFIX)) {
            mapperXmlPath.substring(CLASSPATH_INCLUDE_JAR_PREFIX.length)
        } else {
            mapperXmlPath
        }
        val fileResources = PathResourceScanner.scanFileResourcesWithSuffix(path, ".xml")

        fileResources.forEach {
            XMLMapperBuilder(
                Files.newInputStream(it.file.toPath()),
                configuration,
                it.file.absolutePath,
                configuration.sqlFragments
            ).parse()
        }
    }

    private fun scanInterfaceMappers(configuration: Configuration, mapperPath: String) {
        if (mapperPath.isBlank()) {
            return
        }

        val resources = PathResourceScanner.scanClassResources(mapperPath)
        val mapperInterfaces = resources.map { it.clazz.java }.filter { it.isInterface }
        mapperInterfaces.forEach {
            if (!configuration.hasMapper(it)) {
                configuration.addMapper(it)
            }
        }
    }

    private fun setEnvironment(
        name: String,
        configuration: Configuration,
        databaseUrl: String,
        username: String,
        password: String
    ) {
        // get dataSource
        val config = HikariConfig().apply {
            this.username = username
            this.password = password
            jdbcUrl = databaseUrl
            maximumPoolSize = MAXIMUM_POOL_SIZE
            minimumIdle = MINIMUM_IDLE
            connectionTimeout = CONNECTION_TIMEOUT
            idleTimeout = IDLE_TIMEOUT
            initializationFailTimeout = 0
        }

        configuration.environment = Environment(
            name,
            KtManagedTransactionFactory(),
            HikariDataSource(config)
        )
    }

    private fun createConfiguration(): Configuration {
        return Configuration().apply {
            isMapUnderscoreToCamelCase = true
            isLazyLoadingEnabled = true
            localCacheScope = LocalCacheScope.STATEMENT
            isCacheEnabled = false
        }
    }

    companion object {
        private const val CLASSPATH_INCLUDE_JAR_PREFIX = "classpath*:"
        private const val ENVIRONMENT_NAME_SUFFIX = "Environment"
        private const val MAXIMUM_POOL_SIZE = 32
        private const val MINIMUM_IDLE = 1
        private const val CONNECTION_TIMEOUT = 5000L
        private const val IDLE_TIMEOUT = 60 * 1000L
        private const val DATABASE_SUFFIX = "Database"
    }
}