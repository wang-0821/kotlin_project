package com.xiao.databse

import com.xiao.base.util.ProxyUtils
import com.xiao.databse.utils.SqlSessionUtils
import org.apache.ibatis.cursor.Cursor
import org.apache.ibatis.executor.BatchResult
import org.apache.ibatis.reflection.ExceptionUtil
import org.apache.ibatis.session.Configuration
import org.apache.ibatis.session.ResultHandler
import org.apache.ibatis.session.RowBounds
import org.apache.ibatis.session.SqlSession
import org.apache.ibatis.session.SqlSessionFactory
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.sql.Connection

/**
 *
 * @author lix wang
 */
class KtManagedSqlSession(
    private val configuration: Configuration,
    sqlSessionFactory: SqlSessionFactory
) : SqlSession {
    private val proxy : SqlSession = Proxy.newProxyInstance(
        KtManagedSqlSession::class.java.classLoader,
        arrayOf(SqlSession::class.java),
        KtSqlSessionInvoker(sqlSessionFactory)
    ) as SqlSession

    override fun close() {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun <T : Any?> selectOne(statement: String?): T {
        return proxy.selectOne(statement)
    }

    override fun <T : Any?> selectOne(statement: String?, parameter: Any?): T {
        return proxy.selectOne(statement, parameter)
    }

    override fun <E : Any?> selectList(statement: String?): MutableList<E> {
        return proxy.selectList(statement)
    }

    override fun <E : Any?> selectList(statement: String?, parameter: Any?): MutableList<E> {
        return proxy.selectList(statement, parameter)
    }

    override fun <E : Any?> selectList(statement: String?, parameter: Any?, rowBounds: RowBounds?): MutableList<E> {
        return proxy.selectList(statement, parameter, rowBounds)
    }

    override fun <K : Any?, V : Any?> selectMap(statement: String?, mapKey: String?): MutableMap<K, V> {
        return proxy.selectMap(statement, mapKey)
    }

    override fun <K : Any?, V : Any?> selectMap(
        statement: String?,
        parameter: Any?,
        mapKey: String?
    ): MutableMap<K, V> {
        return proxy.selectMap(statement, parameter, mapKey)
    }

    override fun <K : Any?, V : Any?> selectMap(
        statement: String?,
        parameter: Any?,
        mapKey: String?,
        rowBounds: RowBounds?
    ): MutableMap<K, V> {
        return proxy.selectMap(statement, parameter, mapKey, rowBounds)
    }

    override fun <T : Any?> selectCursor(statement: String?): Cursor<T> {
        return proxy.selectCursor(statement)
    }

    override fun <T : Any?> selectCursor(statement: String?, parameter: Any?): Cursor<T> {
        return proxy.selectCursor(statement, parameter)
    }

    override fun <T : Any?> selectCursor(statement: String?, parameter: Any?, rowBounds: RowBounds?): Cursor<T> {
        return proxy.selectCursor(statement, parameter, rowBounds)
    }

    override fun select(statement: String?, parameter: Any?, handler: ResultHandler<*>?) {
        return proxy.select(statement, parameter, handler)
    }

    override fun select(statement: String?, handler: ResultHandler<*>?) {
        return proxy.select(statement, handler)
    }

    override fun select(statement: String?, parameter: Any?, rowBounds: RowBounds?, handler: ResultHandler<*>?) {
        return proxy.select(statement, parameter, rowBounds, handler)
    }

    override fun insert(statement: String?): Int {
        return proxy.insert(statement)
    }

    override fun insert(statement: String?, parameter: Any?): Int {
        return proxy.insert(statement, parameter)
    }

    override fun update(statement: String?): Int {
        return proxy.update(statement)
    }

    override fun update(statement: String?, parameter: Any?): Int {
        return proxy.update(statement, parameter)
    }

    override fun delete(statement: String?): Int {
        return proxy.delete(statement)
    }

    override fun delete(statement: String?, parameter: Any?): Int {
        return proxy.delete(statement, parameter)
    }

    override fun commit() {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun commit(force: Boolean) {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun rollback() {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun rollback(force: Boolean) {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun flushStatements(): MutableList<BatchResult> {
        return proxy.flushStatements()
    }

    override fun clearCache() {
        return proxy.clearCache()
    }

    override fun getConfiguration(): Configuration {
        return configuration
    }

    override fun <T : Any?> getMapper(type: Class<T>?): T {
        return configuration.getMapper(type, this)
    }

    override fun getConnection(): Connection {
        return proxy.connection
    }

    private class KtSqlSessionInvoker(private val sqlSessionFactory: SqlSessionFactory) : InvocationHandler {
        override fun invoke(proxy: Any?, method: Method, args: Array<Any?>?): Any? {
            val sqlSession = SqlSessionUtils.getSqlSession(sqlSessionFactory)
            try {
                val result = ProxyUtils.invoke(sqlSession, method, args)
                if (!SqlSessionUtils.isTransactional(sqlSessionFactory, sqlSession)) {
                    sqlSession.commit(true)
                }
                return result
            } catch (throwable: Throwable) {
                 throw ExceptionUtil.unwrapThrowable(throwable)
            } finally {
                if (sqlSession != null
                    && !SqlSessionUtils.isTransactional(sqlSessionFactory, sqlSession)) {
                    sqlSession.close()
                }
            }
        }
    }
}