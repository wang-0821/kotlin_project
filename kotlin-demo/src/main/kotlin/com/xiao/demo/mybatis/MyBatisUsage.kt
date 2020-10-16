package com.xiao.demo.mybatis

import com.xiao.base.resource.PathResourceScanner
import com.xiao.base.resource.ResourceMatcher
import com.xiao.demo.mybatis.mapper.UserMapper
import com.xiao.demo.mybatis.mapper.UserMapperV2
import org.apache.ibatis.builder.xml.XMLMapperBuilder
import org.apache.ibatis.session.Configuration
import org.apache.ibatis.session.SqlSessionFactory
import org.apache.ibatis.session.SqlSessionFactoryBuilder
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

    fun sqlSessionFactory(mapperXmlPath: String): SqlSessionFactory {
        if (sqlSessionFactory == null) {
            synchronized(this::class) {
                if (sqlSessionFactory == null) {
                    sqlSessionFactory = createSqlSessionFactory(mapperXmlPath)
                }
                return sqlSessionFactory!!
            }
        } else {
            return sqlSessionFactory!!
        }
    }

    private fun createSqlSessionFactory(mapperXmlPath: String): SqlSessionFactory {
        val configuration = Configuration()
        configuration.isMapUnderscoreToCamelCase = true
        configuration.isLazyLoadingEnabled = true
        scanXmlMapper(configuration, mapperXmlPath)
        val sqlSessionFactoryBuilder = SqlSessionFactoryBuilder()
        return sqlSessionFactoryBuilder.build(configuration)
    }

    private fun scanXmlMapper(configuration: Configuration, mapperXmlPath: String) {
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

    companion object {
        const val CLASSPATH_INCLUDE_JAR_PREFIX = "classpath*:"
        @Volatile var sqlSessionFactory: SqlSessionFactory? = null
    }
}

fun main() {
    val obj = MyBatisUsage()
    val sqlSessionFactory = obj.sqlSessionFactory("classpath*:mybatis/mapper/")
    obj.queryUser(sqlSessionFactory)
}