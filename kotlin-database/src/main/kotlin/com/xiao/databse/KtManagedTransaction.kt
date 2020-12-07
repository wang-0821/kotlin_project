package com.xiao.databse

import com.xiao.databse.utils.DataSourceUtils
import com.xiao.databse.utils.TransactionalUtils
import org.apache.ibatis.transaction.Transaction
import java.sql.Connection
import javax.sql.DataSource

/**
 *
 * @author lix wang
 */
class KtManagedTransaction(private val dataSource: DataSource) : Transaction {
    private var connection: Connection? = null
    private var autoCommit: Boolean = false
    private var isTransactional: Boolean = false
    
    override fun getConnection(): Connection {
        if (connection == null) {
            openConnection()
        }
        return connection!!
    }

    override fun commit() {
        if (connection != null && !isTransactional && !autoCommit) {
            connection!!.commit()
        }
    }

    override fun rollback() {
        if (connection != null && !isTransactional && !autoCommit) {
            connection!!.rollback()
        }
    }

    override fun close() {
        if (connection != null) {
            DataSourceUtils.releaseConnection(dataSource, connection!!)
        }
    }

    override fun getTimeout(): Int? {
        val connectionWrapper = TransactionalUtils.getResource<ConnectionWrapper>(dataSource)
        val timeout = connectionWrapper?.timeUnit?.toSeconds(connectionWrapper.timeout ?: 0)?.toInt()
        return if (timeout != null && timeout > 0) {
            timeout
        } else {
            null
        }
    }

    private fun openConnection() {
        try {
            connection = DataSourceUtils.getConnection(dataSource)
            autoCommit = connection!!.autoCommit
            isTransactional = DataSourceUtils.isTransactional(dataSource, connection!!)
        } catch (e: Exception) {
            throw IllegalStateException("${this.javaClass.simpleName} get connection failed.", e)
        }
    }
}