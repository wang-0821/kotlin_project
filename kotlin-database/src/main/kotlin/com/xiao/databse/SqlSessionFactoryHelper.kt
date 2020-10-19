package com.xiao.databse

import com.xiao.base.resource.PathResourceScanner
import com.xiao.base.resource.ResourceMatcher
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.apache.ibatis.builder.xml.XMLMapperBuilder
import org.apache.ibatis.mapping.Environment
import org.apache.ibatis.session.Configuration
import org.apache.ibatis.session.SqlSessionFactory
import org.apache.ibatis.session.SqlSessionFactoryBuilder
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory
import java.io.File
import java.nio.file.Files

/**
 *
 * @author lix wang
 */
object SqlSessionFactoryHelper {
    private const val CLASSPATH_INCLUDE_JAR_PREFIX = "classpath*:"
    private const val ENVIRONMENT_NAME_SUFFIX = "Environment"
    private const val MAXIMUM_POOL_SIZE = 32
    private const val MINIMUM_IDLE = 1
    private const val CONNECTION_TIMEOUT = 5000L
    private const val IDLE_TIMEOUT = 60 * 1000L

    fun createSqlSessionFactory(
        name: String,
        mapperXmlPath: String,
        mapperPath: String,
        databaseUrl: String,
        username: String,
        password: String
    ): SqlSessionFactory {
        val configuration = Configuration()
        configuration.isMapUnderscoreToCamelCase = true
        configuration.isLazyLoadingEnabled = true

        // scan mappers
        scanXmlMappers(configuration, mapperXmlPath)
        scanMappers(configuration, mapperPath)

        setEnvironment(name, configuration, databaseUrl, username, password)
        val sqlSessionFactoryBuilder = SqlSessionFactoryBuilder()
        return sqlSessionFactoryBuilder.build(configuration)
    }

    private fun scanXmlMappers(configuration: Configuration, mapperXmlPath: String) {
        val path = if (mapperXmlPath.startsWith(CLASSPATH_INCLUDE_JAR_PREFIX)) {
            mapperXmlPath.substring(CLASSPATH_INCLUDE_JAR_PREFIX.length)
        } else {
            mapperXmlPath
        }
        val fileResources = PathResourceScanner.scanFileResourcesByPackage(
            path,
            object : ResourceMatcher {
                override fun matchingDirectory(file: File): Boolean {
                    return true
                }

                override fun matchingFile(file: File): Boolean {
                    return file.name.endsWith(".xml")
                }
            }
        )

        fileResources.forEach {
            XMLMapperBuilder(
                Files.newInputStream(it.file.toPath()),
                configuration,
                it.file.absolutePath,
                configuration.sqlFragments
            ).parse()
        }
    }

    private fun scanMappers(configuration: Configuration, mapperPath: String) {
        val resources = PathResourceScanner.scanClassResourcesByPackage(mapperPath)
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
            jdbcUrl = databaseUrl
            this.username = username
            this.password = password
            maximumPoolSize = MAXIMUM_POOL_SIZE
            minimumIdle = MINIMUM_IDLE
            connectionTimeout = CONNECTION_TIMEOUT
            idleTimeout = IDLE_TIMEOUT
            initializationFailTimeout = 0
        }

        configuration.environment = Environment(
            name + ENVIRONMENT_NAME_SUFFIX,
            JdbcTransactionFactory(),
            HikariDataSource(config)
        )
    }
}