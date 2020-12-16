package com.xiao.databse.testing

import com.xiao.base.logging.KtLogger
import com.xiao.base.logging.LoggerType
import com.xiao.base.logging.Logging
import com.xiao.base.resource.PathResourceScanner
import com.xiao.databse.BaseDatabase
import com.xiao.databse.annotation.KtMapperTables
import org.apache.ibatis.jdbc.ScriptRunner
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import java.io.FileInputStream
import java.io.InputStreamReader
import kotlin.reflect.KClass

/**
 *
 * @author lix wang
 */
class TablesMigrateExtension : BeforeEachCallback {
    override fun beforeEach(context: ExtensionContext?) {
        TestResourceHolder.databaseAnnotations.forEach { ktTestDatabase ->
            val database = TestResourceHolder.databaseInstances[ktTestDatabase.database]
                ?: throw IllegalStateException("Not migrated database: ${ktTestDatabase.database.simpleName}.")
            migrate(database, database.datasetPath(), extractTables(ktTestDatabase.mappers))
        }
    }

    private fun extractTables(mappers: Array<KClass<*>>): Set<String> {
        return mappers
            .flatMap {
                it.java.getAnnotation(KtMapperTables::class.java)?.value?.toList() ?: listOf()
            }
            .toSet()
    }

    private fun migrate(database: BaseDatabase, dataSetPath: String, tables: Set<String>) {
        val connection = database.dataSource().connection
        try {
            val sqlFiles = PathResourceScanner.scanFileResourcesWithSuffix(dataSetPath, ".sql")
                .filter {
                    tables.contains(it.file.name)
                }
            if (sqlFiles.size != tables.size) {
                val lackSqlFileNames = tables.toMutableSet()
                lackSqlFileNames.removeAll(sqlFiles.map { it.file.name })
                throw IllegalStateException(
                    "Lack of migrate sql files of ${lackSqlFileNames.joinToString(", ")}."
                )
            }

            val scriptRunner = ScriptRunner(connection)
            sqlFiles
                .forEach { ktFileResource ->
                    scriptRunner.runScript(InputStreamReader(FileInputStream(ktFileResource.file)))
                    TestResourceHolder.addMigratedTable(database.name(), ktFileResource.file.name)
                }
            log.info("Migrate database: ${database.name()}, sql files: ${tables.joinToString(", ")} succeed.")
        } finally {
            connection?.close()
        }
    }

    @KtLogger(LoggerType.TEST_DATA_SOURCE)
    companion object : Logging()
}