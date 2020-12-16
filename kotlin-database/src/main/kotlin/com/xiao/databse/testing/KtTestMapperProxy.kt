package com.xiao.databse.testing

import com.xiao.databse.KtMapperProxy
import com.xiao.databse.annotation.KtMapperTables
import java.lang.reflect.Method

/**
 *
 * @author lix wang
 */
class KtTestMapperProxy<T>(
    clazz: Class<T>,
    mapper: T,
    private val databaseName: String
) : KtMapperProxy<T>(clazz, mapper) {
    override fun invoke(proxy: Any, method: Method, args: Array<Any?>?): Any? {
        if (!TestResourceHolder.checkDataSourceMigrated(databaseName)) {
            throw IllegalStateException("Not migrate database: $databaseName.")
        }
        val sqlTables = clazz.getAnnotation(KtMapperTables::class.java)?.value?.toList() ?: listOf()
        if (!TestResourceHolder.checkTablesMigrated(databaseName, sqlTables)) {
            throw IllegalStateException("Mapper ${clazz.simpleName} not reset.")
        }
        return super.invoke(proxy, method, args)
    }
}