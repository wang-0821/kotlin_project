package com.xiao.databse

import org.apache.ibatis.session.SqlSessionFactory
import java.lang.reflect.Proxy
import java.util.concurrent.ConcurrentHashMap

/**
 *
 * @author lix wang
 */
object DataSourceHelper {
    private val mappers = ConcurrentHashMap<Class<*>, Any?>()

    @Suppress("UNCHECKED_CAST")
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

    @Suppress("UNCHECKED_CAST")
    private fun <T> createMapper(sqlSessionFactory: SqlSessionFactory, clazz: Class<T>): T {
        return Proxy.newProxyInstance(
            clazz.classLoader,
            arrayOf(clazz),
            KtMapperProxy(
                sqlSessionFactory.configuration.getMapper(
                    clazz,
                    sqlSessionFactory.openSession()
                )
            )
        ) as T
    }
}