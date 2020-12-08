package com.xiao.databse.testing

import javax.sql.DataSource

/**
 *
 * @author lix wang
 */
object TestResourceHolder {
    private var migratedDataSources: List<DataSource> = listOf()
    private var migrateTables: Map<String, Set<String>> = mapOf()

    fun checkDataSourceMigrated(dataSource: DataSource): Boolean {
        return migratedDataSources.contains(dataSource)
    }

    fun checkTablesMigrated(databaseName: String, tables: List<String>): Boolean {
        return (migrateTables[databaseName] ?: setOf()).containsAll(tables)
    }
}