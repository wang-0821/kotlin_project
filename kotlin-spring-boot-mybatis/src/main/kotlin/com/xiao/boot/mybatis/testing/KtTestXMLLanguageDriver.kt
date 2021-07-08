package com.xiao.boot.mybatis.testing

import org.apache.ibatis.mapping.SqlSource
import org.apache.ibatis.parsing.XNode
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver
import org.apache.ibatis.session.Configuration

class KtTestXMLLanguageDriver : XMLLanguageDriver() {
    override fun createSqlSource(configuration: Configuration, script: XNode, parameterType: Class<*>?): SqlSource {
        return KtTestSqlSource(configuration, super.createSqlSource(configuration, script, parameterType))
    }

    override fun createSqlSource(configuration: Configuration, script: String, parameterType: Class<*>?): SqlSource {
        return KtTestSqlSource(configuration, super.createSqlSource(configuration, script, parameterType))
    }
}