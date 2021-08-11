package xiao.databse.testing

import org.apache.ibatis.jdbc.ScriptRunner
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import xiao.base.logging.KtLogger
import xiao.base.logging.LoggerType
import xiao.base.logging.Logging
import xiao.beans.resource.PathResourceScanner
import xiao.databse.annotation.KtMapperTables
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

    private fun migrate(database: xiao.databse.BaseDatabase, dataSetPath: String, tables: Set<String>) {
        val connection = database.dataSource().connection
        try {
            val sqlFiles = PathResourceScanner.scanFileResourcesWithSuffix(dataSetPath, ".sql")
                .filter {
                    tables.contains(it.file.nameWithoutExtension)
                }
            if (sqlFiles.size != tables.size) {
                val lackSqlFileNames = tables.toMutableSet()
                lackSqlFileNames.removeAll(sqlFiles.map { it.file.nameWithoutExtension })
                throw IllegalStateException(
                    "Lack of migrate sql files of [${lackSqlFileNames.joinToString(", ")}]."
                )
            }

            val scriptRunner = ScriptRunner(connection)
            scriptRunner.setLogWriter(null)
            sqlFiles
                .forEach { ktFileResource ->
                    val fileName = ktFileResource.file.nameWithoutExtension
                    // clear data grip of the table
                    connection
                        .createStatement()
                        .executeUpdate("DELETE FROM $fileName;")
                    scriptRunner.runScript(InputStreamReader(FileInputStream(ktFileResource.file)))
                    TestDataSourceContainer.addMigratedTable(database.name(), fileName)
                }
            log.info("Migrate database: ${database.name()}, sql files: [${tables.joinToString(", ")}] succeed.")
        } finally {
            connection?.close()
        }
    }

    @KtLogger(LoggerType.TEST_DATA_SOURCE)
    companion object : Logging()
}