package com.xiao.databse.utils

import com.xiao.databse.KtMapperProxy
import com.xiao.databse.testing.KtTestMapperProxy
import org.apache.ibatis.session.SqlSessionFactory
import java.lang.reflect.Proxy
import java.util.concurrent.ConcurrentHashMap

/**
 *
 * @author lix wang
 */
@Suppress("UNCHECKED_CAST")
object MapperUtils {
    private val mappers = ConcurrentHashMap<Class<*>, Any?>()

    fun <T> getTestMapper(sqlSessionFactory: SqlSessionFactory, clazz: Class<T>): T {
        if (mappers[clazz] != null) {
            return mappers[clazz] as T
        }

        synchronized(mappers) {
            if (mappers[clazz] != null) {
                return mappers[clazz] as T
            }
            val mapper = createTestMapper(sqlSessionFactory, clazz)
            mappers[clazz] = mapper
            return mapper
        }
    }

    fun <T> getMapper(sqlSessionFactory: SqlSessionFactory, clazz: Class<T>): T {
        if (mappers[clazz] != null) {
            return mappers[clazz] as T
        }

        synchronized(mappers) {
            if (mappers[clazz] != null) {
                return mappers[clazz] as T
            }
            val mapper = createMapper(sqlSessionFactory, clazz)
            mappers[clazz] = mapper
            return mapper
        }
    }

    private fun <T> createMapper(sqlSessionFactory: SqlSessionFactory, clazz: Class<T>): T {
        return Proxy.newProxyInstance(
            clazz.classLoader,
            arrayOf(clazz),
            KtMapperProxy(
                clazz,
                sqlSessionFactory.configuration.getMapper(
                    clazz,
                    sqlSessionFactory.openSession()
                )
            )
        ) as T
    }

    private fun <T> createTestMapper(sqlSessionFactory: SqlSessionFactory, clazz: Class<T>): T {
        return Proxy.newProxyInstance(
            clazz.classLoader,
            arrayOf(clazz),
            KtTestMapperProxy(
                clazz,
                sqlSessionFactory.configuration.getMapper(
                    clazz,
                    sqlSessionFactory.openSession()
                ),
                sqlSessionFactory.configuration.databaseId,
                sqlSessionFactory.configuration.environment.dataSource
            )
        ) as T
    }
}