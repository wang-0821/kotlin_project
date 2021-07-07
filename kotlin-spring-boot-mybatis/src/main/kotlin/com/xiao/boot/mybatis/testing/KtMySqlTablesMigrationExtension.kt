package com.xiao.boot.mybatis.testing

import com.xiao.base.util.ThreadUtils
import com.xiao.boot.base.testing.TestSpringContextUtils
import com.xiao.boot.mybatis.database.BaseDatabase
import com.xiao.boot.mybatis.database.BaseDatabase.Companion.dataSourceName
import org.apache.ibatis.jdbc.ScriptRunner
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import java.io.FileInputStream
import java.io.InputStreamReader
import javax.sql.DataSource
import kotlin.reflect.KClass

/**
 *
 * @author lix wang
 */
class KtMySqlTablesMigrationExtension : BeforeAllCallback {
    override fun beforeAll(context: ExtensionContext) {
        val applicationContext = TestSpringContextUtils.getTestContext(context.requiredTestClass).applicationContext
        val testDatabaseTablesMap = extractDatabasesTables(context.requiredTestClass)
        testDatabaseTablesMap
            .map { (databaseClass, tables) ->
                val database = applicationContext.getBean(databaseClass.java)
                val dataSourceName = dataSourceName(database.name)
                val dataSource = applicationContext.getBean(dataSourceName, DataSource::class.java)
                ThreadUtils.DEFAULT_EXECUTOR.submit {
                    runDatabaseScripts(database, dataSource, tables)
                }
            }.forEach {
                it.get()
            }
    }

    private fun runDatabaseScripts(
        database: BaseDatabase,
        dataSource: DataSource,
        tables: Set<String>
    ) {
        val dataFiles = tables.map { database.getTestTableDataScript(it).file }
        if (dataFiles.isNotEmpty()) {
            val connection = dataSource.connection
            val runner = ScriptRunner(connection)
                .apply {
                    setSendFullScript(true)
                    setLogWriter(null)
                }
            dataFiles
                .forEach { file ->
                    connection
                        .createStatement()
                        .executeUpdate("DELETE FROM ${file.nameWithoutExtension};")
                    runner.runScript(InputStreamReader(FileInputStream(file)))
                }
        }
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
}