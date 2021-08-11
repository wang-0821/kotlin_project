package xiao.databse.utils

import org.apache.ibatis.session.SqlSession
import org.apache.ibatis.session.SqlSessionFactory
import xiao.databse.TransactionHandler

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

    fun isTransactional(sqlSessionFactory: SqlSessionFactory, sqlSession: SqlSession): Boolean {
        return TransactionalUtils.getResource<SqlSession>(sqlSessionFactory) == sqlSession
    }

    private fun prepareTransactionalSqlSession(sqlSessionFactory: SqlSessionFactory): SqlSession {
        var sqlSession = TransactionalUtils.getResource<SqlSession>(sqlSessionFactory)
        if (sqlSession != null) {
            return sqlSession
        }
        sqlSession = sqlSessionFactory.openSession()
        TransactionalUtils.setResource(sqlSessionFactory, sqlSession)
        TransactionalUtils.registerTransactionHandler(
            SqlSessionTransactionHandler(sqlSessionFactory, sqlSession)
        )
        return sqlSession
    }

    class SqlSessionTransactionHandler(
        private val sqlSessionFactory: SqlSessionFactory,
        private val sqlSession: SqlSession
    ) : TransactionHandler {
        override fun beforeTransaction() {
            sqlSession.commit()
        }

        override fun commit() {
            DataSourceUtils.findTransactionalConnection(sqlSession.configuration.environment.dataSource)?.commit()
                ?: throw IllegalStateException("SqlSession $sqlSession can't find transaction connection.")
        }

        override fun rollback(throwable: Throwable) {
            val wrapper = TransactionalUtils.checkAndGetTransactionWrapper()
            if (TransactionalUtils.needRollback(throwable, wrapper.rollbackFor, wrapper.noRollbackFor)) {
                DataSourceUtils.findTransactionalConnection(sqlSession.configuration.environment.dataSource)?.rollback()
            }
        }

        override fun afterTransaction() {
            releaseSqlSession(sqlSessionFactory, sqlSession)
            sqlSession.close()
            val dataSource = sqlSession.configuration.environment.dataSource
            DataSourceUtils.findTransactionalConnection(dataSource)?.let {
                DataSourceUtils.releaseConnection(dataSource, it)
                it.close()
            }
        }
    }
}