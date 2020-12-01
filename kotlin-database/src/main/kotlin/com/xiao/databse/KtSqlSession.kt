package com.xiao.databse

import com.xiao.databse.utils.SqlSessionUtils
import org.apache.ibatis.session.SqlSessionFactory
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method

/**
 *
 * @author lix wang
 */
class KtSqlSession(private val sqlSessionFactory: SqlSessionFactory) : InvocationHandler {
    override fun invoke(proxy: Any, method: Method, args: Array<Any?>?): Any {
        val sqlSession = SqlSessionUtils.getSqlSession(sqlSessionFactory)
        return method.invoke(sqlSession, args)
    }
}