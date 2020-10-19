package com.xiao.demo.mybatis

import com.xiao.base.resource.PathResourceScanner
import com.xiao.base.resource.ResourceMatcher
import com.xiao.demo.mybatis.mapper.UserMapper
import com.xiao.demo.mybatis.mapper.UserMapperV2
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
class MyBatisUsage {
    fun queryUser(sqlSessionFactory: SqlSessionFactory) {
        val sqlSession = sqlSessionFactory.openSession()
        val userMapper = sqlSessionFactory.configuration.getMapper(UserMapper::class.java, sqlSession)
        val userMapperV2 = sqlSessionFactory.configuration.getMapper(UserMapperV2::class.java, sqlSession)
        println(userMapper.getById(1))
        println(userMapperV2.getById(1))
    }

    fun sqlSessionFactory(mapperXmlPath: String, mapperPath: String): SqlSessionFactory {
        if (sqlSessionFactory == null) {
            synchronized(this::class) {
                if (sqlSessionFactory == null) {
                    sqlSessionFactory = createSqlSessionFactory(mapperXmlPath, mapperPath)
                }
                return sqlSessionFactory!!
            }
        } else {
            return sqlSessionFactory!!
        }
    }

    private fun createSqlSessionFactory(mapperXmlPath: String, mapperPath: String): SqlSessionFactory {
        val configuration = Configuration()
        configuration.isMapUnderscoreToCamelCase = true
        configuration.isLazyLoadingEnabled = true

        // scan mappers
        scanXmlMappers(configuration, mapperXmlPath)
        scanMappers(configuration, mapperPath)

        setEnvironment(configuration)
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

    private fun setEnvironment(configuration: Configuration) {
        // get dataSource
        val config = HikariConfig().apply {
            jdbcUrl = DATABASE_URL
            username = USERNAME
            password = PASSWORD
            maximumPoolSize = MAXIMUM_POOL_SIZE
            minimumIdle = MINIMUM_IDLE
            connectionTimeout = CONNECTION_TIMEOUT
            idleTimeout = IDLE_TIMEOUT
            initializationFailTimeout = 0
        }

        configuration.environment = Environment(
            NAME + "Environment",
            JdbcTransactionFactory(),
            HikariDataSource(config)
        )
    }

    companion object {
        const val CLASSPATH_INCLUDE_JAR_PREFIX = "classpath*:"
        @Volatile var sqlSessionFactory: SqlSessionFactory? = null
        const val NAME = "demo"
        const val DATABASE_URL = "jdbc:mysql://localhost:3306/lix_database_demo"
        const val USERNAME = "root"
        const val PASSWORD = "123456"
        const val MAXIMUM_POOL_SIZE = 32
        const val MINIMUM_IDLE = 1
        const val CONNECTION_TIMEOUT = 5000L
        const val IDLE_TIMEOUT = 60 * 1000L
    }
}

fun main() {
    val obj = MyBatisUsage()
    val sqlSessionFactory = obj.sqlSessionFactory(
        "classpath*:mybatis/mapper/", "com.xiao.demo.mybatis.mapper"
    )
    obj.queryUser(sqlSessionFactory)
}