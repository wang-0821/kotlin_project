package xiao.databse.utils

import org.apache.ibatis.session.SqlSessionFactory
import xiao.databse.KtMapperProxy
import xiao.databse.testing.KtTestMapperProxy
import xiao.databse.testing.TestDataSourceContainer
import java.lang.reflect.Proxy
import java.util.concurrent.ConcurrentHashMap

/**
 *
 * @author lix wang
 */
@Suppress("UNCHECKED_CAST")
object MapperUtils {
    private val mappers = ConcurrentHashMap<Class<*>, Any?>()

    @JvmStatic
    fun <T> getTestMapper(clazz: Class<T>): T {
        if (mappers[clazz] != null) {
            return mappers[clazz] as T
        }

        synchronized(mappers) {
            if (mappers[clazz] != null) {
                return mappers[clazz] as T
            }
            val mapper = createTestMapper(clazz)
            mappers[clazz] = mapper
            return mapper
        }
    }

    @JvmStatic
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

    private fun <T> createTestMapper(clazz: Class<T>): T {
        val database = TestDataSourceContainer.databaseAnnotations
            .firstOrNull {
                it.mappers
                    .any {
                        it.java == clazz
                    }
            }?.database
            ?.let {
                TestDataSourceContainer.getDatabase(it)
            }
            ?: throw IllegalStateException("Can't find database for mapper: ${clazz.simpleName}.")

        val sqlSessionFactory = database.sqlSessionFactory()
        return Proxy.newProxyInstance(
            clazz.classLoader,
            arrayOf(clazz),
            KtTestMapperProxy(
                clazz,
                sqlSessionFactory.configuration.getMapper(
                    clazz,
                    sqlSessionFactory.openSession()
                ),
                database.name()
            )
        ) as T
    }
}