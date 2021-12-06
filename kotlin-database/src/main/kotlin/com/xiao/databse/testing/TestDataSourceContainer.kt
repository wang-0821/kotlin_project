package com.xiao.databse.testing

import com.xiao.database.annotation.KtTestDatabase
import com.xiao.databse.BaseDatabase
import kotlin.reflect.KClass

/**
 *
 * @author lix wang
 */
object TestDataSourceContainer {
    var databaseInstances = mutableMapOf<KClass<out BaseDatabase>, BaseDatabase>()
        private set
    var databaseAnnotations = mutableSetOf<KtTestDatabase>()
        private set
    private var migratedDatabaseNames = mutableSetOf<String>()
    private var migratedTables = mutableMapOf<String, MutableSet<String>>()

    fun checkDataSourceMigrated(databaseName: String): Boolean {
        return migratedDatabaseNames.contains(databaseName)
    }

    fun checkTablesMigrated(databaseName: String, tables: List<String>): Boolean {
        return (migratedTables[databaseName] ?: mutableSetOf()).containsAll(tables)
    }

    fun addDatabaseAnnotations(annotations: List<KtTestDatabase>) {
        databaseAnnotations.addAll(annotations)
    }

    fun addDatabaseInstance(clazz: KClass<out BaseDatabase>, database: BaseDatabase) {
        if (databaseInstances[clazz] != null) {
            throw IllegalArgumentException("Duplicate database instance for ${database.name()}.")
        }
        databaseInstances[clazz] = database
    }

    fun addMigratedDatabase(databaseName: String) {
        migratedDatabaseNames.add(databaseName)
    }

    fun addMigratedTable(databaseName: String, table: String) {
        if (!migratedDatabaseNames.contains(databaseName)) {
            throw IllegalArgumentException("Not migrate database: $databaseName.")
        }
        migratedTables[databaseName]?.add(table) ?: kotlin.run {
            migratedTables[databaseName] = mutableSetOf(table)
        }
    }

    fun getDatabase(kClass: KClass<out BaseDatabase>): BaseDatabase {
        return databaseInstances[kClass]
            ?: throw IllegalArgumentException("Database ${kClass.simpleName} not registered.")
    }
}