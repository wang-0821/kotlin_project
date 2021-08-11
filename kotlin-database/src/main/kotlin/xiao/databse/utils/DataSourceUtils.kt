package xiao.databse.utils

import xiao.databse.ConnectionWrapper
import java.sql.Connection
import javax.sql.DataSource

/**
 *
 * @author lix wang
 */
object DataSourceUtils {
    /**
     * 如果是事务资源，那么让只需要将事务缓存中移除，具体资源由事务处理方关闭。
     */
    fun releaseConnection(dataSource: DataSource, connection: Connection) {
        if (isTransactional(dataSource, connection)) {
            TransactionalUtils.removeResource(dataSource)
        } else {
            connection.close()
        }
    }

    fun getConnection(dataSource: DataSource): Connection {
        return if (TransactionalUtils.isTransactional(dataSource)) {
            prepareTransactionConnection(dataSource)
        } else {
            dataSource.connection
        } ?: throw IllegalStateException("DataSource $dataSource get connection failed.")
    }

    fun findTransactionalConnection(dataSource: DataSource): Connection? {
        return TransactionalUtils.getResource<ConnectionWrapper>(dataSource)?.connection
    }

    fun isTransactional(dataSource: DataSource, connection: Connection): Boolean {
        return TransactionalUtils.getResource<ConnectionWrapper>(dataSource)?.connection == connection
    }

    private fun prepareTransactionConnection(dataSource: DataSource): Connection {
        var connection = TransactionalUtils.getResource<ConnectionWrapper>(dataSource)?.connection
        if (connection != null) {
            return connection
        }
        val wrapper = TransactionalUtils.checkAndGetTransactionWrapper()
        connection = dataSource.connection
        TransactionalUtils.setResource(
            dataSource,
            ConnectionWrapper(connection).apply {
                timeout = wrapper.timeout
                timeUnit = wrapper.timeUnit
            }
        )
        if (connection.autoCommit) {
            connection.autoCommit = false
        }
        if (connection.transactionIsolation != wrapper.isolation.level) {
            connection.transactionIsolation = wrapper.isolation.level
        }
        return connection
    }
}