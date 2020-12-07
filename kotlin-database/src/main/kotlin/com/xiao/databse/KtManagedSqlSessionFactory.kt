package com.xiao.databse

import org.apache.ibatis.session.Configuration
import org.apache.ibatis.session.ExecutorType
import org.apache.ibatis.session.SqlSession
import org.apache.ibatis.session.SqlSessionFactory
import org.apache.ibatis.session.TransactionIsolationLevel
import org.apache.ibatis.session.defaults.DefaultSqlSessionFactory
import java.sql.Connection

/**
 *
 * @author lix wang
 */
class KtManagedSqlSessionFactory(private val configuration: Configuration) : SqlSessionFactory {
    private val delegate = DefaultSqlSessionFactory(configuration)

    override fun openSession(): SqlSession {
        return openSqlSession(null, null, false)
    }

    override fun openSession(autoCommit: Boolean): SqlSession {
        return openSqlSession(null, null, autoCommit)
    }

    override fun openSession(connection: Connection?): SqlSession {
        throw UnsupportedOperationException()
    }

    override fun openSession(level: TransactionIsolationLevel?): SqlSession {
        return openSqlSession(null, level, false)
    }

    override fun openSession(execType: ExecutorType?): SqlSession {
        return openSqlSession(execType, null, false)
    }

    override fun openSession(execType: ExecutorType?, autoCommit: Boolean): SqlSession {
        return openSqlSession(execType, null, autoCommit)
    }

    override fun openSession(execType: ExecutorType?, level: TransactionIsolationLevel?): SqlSession {
        return openSqlSession(execType, level, false)
    }

    override fun openSession(execType: ExecutorType?, connection: Connection?): SqlSession {
        throw UnsupportedOperationException()
    }

    override fun getConfiguration(): Configuration {
        return configuration
    }

    private fun openSqlSession(
        executorType: ExecutorType?,
        transactionIsolationLevel: TransactionIsolationLevel?,
        autoCommit: Boolean
    ): SqlSession {
        return KtManagedSqlSession(configuration, delegate)
    }
}