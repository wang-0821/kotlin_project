package com.xiao.databse.utils

import com.xiao.databse.TransactionHandler
import org.apache.ibatis.session.SqlSession
import org.apache.ibatis.session.SqlSessionFactory

/**
 *
 * @author lix wang
 */
object SqlSessionUtils {
    fun getSqlSession(sqlSessionFactory: SqlSessionFactory): SqlSession {
        return if (TransactionalUtils.isTransactional(sqlSessionFactory.configuration.environment.dataSource)) {
            prepareTransactionalSqlSession(sqlSessionFactory)
        } else {
            sqlSessionFactory.openSession()
        } ?: throw IllegalStateException("SqlSessionFactory $sqlSessionFactory get sqlSession failed.")
    }

    fun releaseSqlSession(sqlSessionFactory: SqlSessionFactory, sqlSession: SqlSession) {
        if (isTransactional(sqlSessionFactory, sqlSession)) {
            TransactionalUtils.removeResource(sqlSessionFactory)
        } else {
            sqlSession.close()
        }
    }

    private fun prepareTransactionalSqlSession(sqlSessionFactory: SqlSessionFactory): SqlSession {
        var sqlSession = TransactionalUtils.getResource<SqlSession>(sqlSessionFactory)
        if (sqlSession != null) {
            return sqlSession
        }
        sqlSession = sqlSessionFactory.openSession()
        TransactionalUtils.setResource(sqlSessionFactory, sqlSession)
        TransactionalUtils.registerTransactionHandler(SqlSessionTransactionHandler(sqlSessionFactory, sqlSession))
        return sqlSession
    }

    private fun isTransactional(sqlSessionFactory: SqlSessionFactory, sqlSession: SqlSession): Boolean {
        return TransactionalUtils.getResource<SqlSession>(sqlSessionFactory) == sqlSession
    }

    class SqlSessionTransactionHandler(
        private val sqlSessionFactory: SqlSessionFactory,
        private val sqlSession: SqlSession
    ) : TransactionHandler {
        override fun beforeCommit() {
            sqlSession.commit()
        }

        override fun commit() {
            sqlSession.commit(true)
        }

        override fun afterCommit() {
            releaseSqlSession(sqlSessionFactory, sqlSession)
            sqlSession.close()
            val dataSource = sqlSession.configuration.environment.dataSource
            val connection = DataSourceUtils.findTransactionalConnection(dataSource)
            if (connection != null) {
                DataSourceUtils.releaseConnection(dataSource, connection)
                connection.close()
            }
        }

        override fun rollback(throwable: Throwable) {
            val wrapper = TransactionalUtils.checkAndGetTransactionWrapper()
            if (TransactionalUtils.needRollback(throwable, wrapper.rollbackFor, wrapper.noRollbackFor)) {
                sqlSession.rollback(true)
            }
        }
    }
}