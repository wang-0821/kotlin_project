package com.xiao.boot.mybatis.testing

import org.apache.commons.lang3.CharUtils
import org.apache.ibatis.mapping.BoundSql
import org.apache.ibatis.mapping.SqlSource

class KtTestSqlSource(private val sqlSource: SqlSource) : SqlSource {
    override fun getBoundSql(parameterObject: Any?): BoundSql {
        val sqlSource = sqlSource.getBoundSql(parameterObject)
        parseSql(sqlSource.sql)
        return sqlSource
    }

    private fun parseSql(sql: String) {
        val upperCaseSql = sql.toUpperCase()
        val tables = getNextWordsByKeyWord("FROM", sql)
        val joinTables = getNextWordsByKeyWord("JOIN", sql)
        val totalTables = tables + joinTables
    }

    private fun getNextWordsByKeyWord(word: String, sql: String): Set<String> {
        val result = mutableSetOf<String>()
        val array = sql.toCharArray()
        var startPos = 0
        while (startPos < sql.length) {
            val matchEndIndex = sql.indexOf(word, startPos)
            if (matchEndIndex < 0) {
                break
            }

            val matchStartIndex = matchEndIndex - word.length + 1
            if (matchStartIndex > 0
                && matchEndIndex < sql.length - 1
                && !CharUtils.isAsciiPrintable(array[matchStartIndex - 1])
                && !CharUtils.isAsciiPrintable(array[matchEndIndex + 1])
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
                if (CharUtils.isAsciiPrintable(array[i])) {
                    if (resultStartIndex < 0) {
                        resultStartIndex = i
                    }
                } else {
                    if (resultStartIndex > 0) {
                        resultEndIndex = i
                        break
                    }
                }
            }

            if (resultStartIndex in 1 until resultEndIndex) {
                return sql.substring(resultStartIndex..resultEndIndex)
            }
        }
        return null
    }
}