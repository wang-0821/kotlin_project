package com.xiao.boot.mybatis.testing

import com.xiao.boot.mybatis.database.BaseDatabase.Companion.DATABASE_NAME_KEY
import org.apache.ibatis.exceptions.PersistenceException
import org.apache.ibatis.mapping.BoundSql
import org.apache.ibatis.mapping.SqlSource
import org.apache.ibatis.session.Configuration
import util.isViewable

class KtTestSqlSource(
    private val configuration: Configuration,
    private val sqlSource: SqlSource
) : SqlSource {
    override fun getBoundSql(parameterObject: Any?): BoundSql {
        val sqlSource = sqlSource.getBoundSql(parameterObject)
        checkRelatedTablesAllMigrated(sqlSource.sql)
        return sqlSource
    }

    private fun checkRelatedTablesAllMigrated(sql: String) {
        val tables = getNextWordsByKeyWord("FROM", sql)
        val joinTables = getNextWordsByKeyWord("JOIN", sql)
        val totalTables = tables + joinTables
        if (totalTables.isNotEmpty()) {
            val databaseName = configuration.variables.getProperty(DATABASE_NAME_KEY)
                ?: throw IllegalStateException("Forget to set $DATABASE_NAME_KEY property in configuration variables.")
            val migratedTables = TestMigrationUtils.migratedTables[databaseName] ?: setOf()

            val notMigratedTables = totalTables.filter { !migratedTables.contains(it) }
            if (notMigratedTables.isNotEmpty()) {
                throw PersistenceException(
                    "Forget to migrate tables: ${notMigratedTables.joinToString()} for database: $databaseName."
                )
            }
        }
    }

    private fun getNextWordsByKeyWord(word: String, sql: String): Set<String> {
        val result = mutableSetOf<String>()
        val array = sql.toCharArray()
        var startPos = 0
        while (startPos < sql.length) {
            val matchStartIndex = sql.indexOf(word, startPos, true)
            if (matchStartIndex < 0) {
                break
            }

            val matchEndIndex = matchStartIndex + word.length - 1
            if (matchStartIndex > 0 &&
                matchEndIndex < sql.length - 1 &&
                !array[matchStartIndex - 1].isViewable() &&
                !array[matchEndIndex + 1].isViewable()
            ) {
                startPos = matchEndIndex + 1
                getNextWord(startPos, sql)
                    ?.let {
                        result.add(it)
                    }
            } else {
                startPos = matchEndIndex + 1
            }
        }
        return result
    }

    private fun getNextWord(startIndex: Int, sql: String): String? {
        val array = sql.toCharArray()
        if (startIndex > 0) {
            var resultStartIndex = -1
            var resultEndIndex = -1
            for (i in startIndex until sql.length) {
                if (array[i].isViewable()) {
                    if (resultStartIndex < 0) {
                        resultStartIndex = i
                    }
                } else {
                    if (resultStartIndex > 0) {
                        resultEndIndex = i - 1
                        break
                    }
                }
            }

            if (resultStartIndex in 0..resultEndIndex && resultEndIndex in resultStartIndex until sql.length) {
                return sql.substring(resultStartIndex..resultEndIndex)
            }
        }
        return null
    }
}