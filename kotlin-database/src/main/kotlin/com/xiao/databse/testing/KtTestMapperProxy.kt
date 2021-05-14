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
        if (!TestDataSourceContainer.checkDataSourceMigrated(databaseName)) {
            throw IllegalStateException("Database $databaseName not migrated.")
        }
        val sqlTables = clazz.getAnnotation(KtMapperTables::class.java)?.value?.toList() ?: listOf()
        if (!TestDataSourceContainer.checkTablesMigrated(databaseName, sqlTables)) {
            throw IllegalStateException("Database $databaseName, tables $sqlTables not migrated.")
        }
        return super.invoke(proxy, method, args)
    }
}