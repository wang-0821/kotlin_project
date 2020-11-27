package com.xiao.databse

import org.apache.ibatis.session.TransactionIsolationLevel
import org.apache.ibatis.transaction.Transaction
import java.sql.Connection
import javax.sql.DataSource

/**
 *
 * @author lix wang
 */
class KtManagedTransaction(private val dataSource: DataSource) : Transaction {
    private var connection: Connection? = null
    private var level: TransactionIsolationLevel? = null

    override fun getConnection(): Connection {
        TODO("Not yet implemented")
    }

    override fun commit() {
        TODO("Not yet implemented")
    }

    override fun rollback() {
        TODO("Not yet implemented")
    }

    override fun close() {
        TODO("Not yet implemented")
    }

    override fun getTimeout(): Int {
        TODO("Not yet implemented")
    }

    private fun openConnection(): Connection {
        try {
            return dataSource.connection
        } catch (e: Exception) {
            throw IllegalStateException("${this.javaClass.simpleName} get connection failed.", e)
        }
    }
}