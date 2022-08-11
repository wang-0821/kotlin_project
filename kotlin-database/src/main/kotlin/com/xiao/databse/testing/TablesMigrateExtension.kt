package com.xiao.databse.testing

import com.xiao.base.logging.KtLogger
import com.xiao.base.logging.LoggerType
import com.xiao.base.logging.Logging
import com.xiao.beans.resource.PathResourceScanner
import com.xiao.databse.BaseDatabase
import com.xiao.databse.annotation.KtMapperTables
import org.apache.ibatis.jdbc.ScriptRunner
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import java.io.FileInputStream
import java.io.InputStreamReader
import kotlin.reflect.KClass

/**
 * 在每个测试方法执行前，先对要用到的表进行数据迁移。
 *
 * @author lix wang
 */
class TablesMigrateExtension : BeforeEachCallback {
    override fun beforeEach(context: ExtensionContext) {
        TestDataSourceContainer.databaseAnnotations.forEach { ktTestDatabase ->
            val database = TestDataSourceContainer.databaseInstances[ktTestDatabase.database]
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
        database.dataSource().connection.use { conn ->
            val sqlFiles = PathResourceScanner.scanFileResourcesWithSuffix(dataSetPath, ".sql")
                .filter {
                    tables.contains(it.file.nameWithoutExtension)
                }
            if (sqlFiles.size != tables.size) {
                val lackSqlFileNames = tables.toMutableSet()
                lackSqlFileNames.removeAll(sqlFiles.map { it.file.nameWithoutExtension }.toSet())
                throw IllegalStateException(
                    "Lack of migrate sql files of [${lackSqlFileNames.joinToString(", ")}]."
                )
            }

            val scriptRunner = ScriptRunner(conn)
            scriptRunner.setLogWriter(null)
            sqlFiles
                .forEach { ktFileResource ->
                    val fileName = ktFileResource.file.nameWithoutExtension
                    // clear data grip of the table
                    conn
                        .createStatement()
                        .executeUpdate("DELETE FROM $fileName;")
                    scriptRunner.runScript(InputStreamReader(FileInputStream(ktFileResource.file)))
                    TestDataSourceContainer.addMigratedTable(database.name(), fileName)
                }
            log.info("Migrate database: ${database.name()}, sql files: [${tables.joinToString(", ")}] succeed.")
        }
    }

    @KtLogger(LoggerType.TEST_DATA_SOURCE)
    companion object : Logging()
}