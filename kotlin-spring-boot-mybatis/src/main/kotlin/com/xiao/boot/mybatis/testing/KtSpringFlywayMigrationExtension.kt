package com.xiao.boot.mybatis.testing

import com.xiao.boot.mybatis.database.BaseDatabase
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import kotlin.reflect.KClass

class KtSpringFlywayMigrationExtension : BeforeAllCallback {
    override fun beforeAll(context: ExtensionContext?) {
        // todo
    }

    private fun extractDatabasesTables(testClass: Class<*>): Map<KClass<out BaseDatabase>, Set<String>> {
        val databases1 = testClass.getAnnotationsByType(TestKtSpringDatabase::class.java)
        val databases2 = testClass.getAnnotationsByType(TestKtSpringDatabases::class.java)
            .flatMap {
                it.databases.toList()
            }
        return (databases1 + databases2)
            .associate { testDatabase ->
                testDatabase.database to testDatabase.mappers
                    .flatMapTo(HashSet()) { mapper ->
                        mapper.java.getAnnotation(TestMapperTables::class.java).tables.toList()
                    }
            }
    }

    companion object {
        @Volatile private var migrated = false
    }
}