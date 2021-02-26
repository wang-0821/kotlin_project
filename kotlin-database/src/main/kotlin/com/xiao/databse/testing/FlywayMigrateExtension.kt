package com.xiao.databse.testing

import com.xiao.base.logging.KtLogger
import com.xiao.base.logging.LoggerType
import com.xiao.base.logging.Logging
import com.xiao.databse.BaseDatabase
import com.xiao.databse.annotation.KtTestDatabase
import com.xiao.databse.annotation.KtTestDatabases
import org.flywaydb.core.Flyway
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import java.util.regex.Pattern
import kotlin.reflect.KClass

/**
 * 在测试过程中，执行且只执行一次数据库结构迁移。
 *
 * @author lix wang
 */
class FlywayMigrateExtension : BeforeAllCallback {
    override fun beforeAll(context: ExtensionContext) {
        if (migrated) {
            return
        }
        synchronized(migrated) {
            if (migrated) {
                return
            }
            migrateDatabase(context)
            migrated = true
        }
    }

    private fun migrateDatabase(context: ExtensionContext) {
        val element = context.element.get()
        val annotations = mutableListOf<KtTestDatabase>()
        element.getAnnotation(KtTestDatabase::class.java)?.let {
            annotations.add(it)
        }
        element.getAnnotation(KtTestDatabases::class.java)?.let {
            annotations.addAll(it.value)
        }
        if (annotations.isEmpty()) {
            throw IllegalStateException("Can't find any ${KtTestDatabase::class.java.simpleName} configuration.")
        }
        TestResourceContainer.addDatabaseAnnotations(annotations)
        migrateDatabase(annotations.map { resolveDatabase(it.database) })
    }

    private fun migrateDatabase(databases: List<BaseDatabase>) {
        val databaseRegex = Pattern.compile("(.*://.*)/(.*)")
        databases.forEach { database ->
            val matcher = databaseRegex.matcher(database.url)
            if (matcher.find() && matcher.groupCount() > 1) {
                Flyway
                    .configure()
                    .dataSource(matcher.group(1), database.username, database.password)
                    .sqlMigrationSuffixes(".sql")
                    .schemas(matcher.group(2))
                    .load()
                    .migrate()
                TestResourceContainer.addMigratedDatabase(database.name())
                log.info("Migrate database: [${database.name()}] succeed.")
            } else {
                throw IllegalArgumentException("Database ${database.name()} url doesn't match pattern.")
            }
        }
    }

    private fun resolveDatabase(databaseClass: KClass<out BaseDatabase>): BaseDatabase {
        return TestResourceContainer.databaseInstances[databaseClass] ?: kotlin.run {
            databaseClass.java.newInstance()
                .also {
                    TestResourceContainer.addDatabaseInstance(databaseClass, it)
                }
        }
    }

    @KtLogger(LoggerType.TEST_DATA_SOURCE)
    companion object : Logging() {
        @Volatile
        private var migrated = false
    }
}