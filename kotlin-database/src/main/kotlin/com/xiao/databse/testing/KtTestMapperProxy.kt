package com.xiao.databse.testing

import com.xiao.databse.KtMapperProxy
import java.lang.reflect.Method
import javax.sql.DataSource

/**
 *
 * @author lix wang
 */
class KtTestMapperProxy<T>(
    clazz: Class<T>,
    mapper: T,
    private val databaseId: String,
    private val dataSource: DataSource
) : KtMapperProxy<T>(clazz, mapper) {
    override fun invoke(proxy: Any, method: Method, args: Array<Any?>?): Any? {
        if (!TestResourceHolder.checkDataSourceMigrated(dataSource)) {
            throw IllegalStateException("Not migrate dataSource: $dataSource.")
        }
        val sqlTables = clazz.getAnnotation(KtSqlTables::class.java)?.value?.toList() ?: listOf()
        if (!TestResourceHolder.checkTablesMigrated(databaseId, sqlTables)) {
            throw IllegalStateException("Mapper ${clazz.simpleName} not reset.")
        }
        return super.invoke(proxy, method, args)
    }
}