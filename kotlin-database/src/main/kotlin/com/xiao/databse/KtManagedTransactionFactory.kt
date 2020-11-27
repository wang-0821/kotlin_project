package com.xiao.databse

import org.apache.ibatis.session.TransactionIsolationLevel
import org.apache.ibatis.transaction.Transaction
import org.apache.ibatis.transaction.TransactionFactory
import java.sql.Connection
import javax.sql.DataSource

/**
 *
 * @author lix wang
 */
class KtManagedTransactionFactory : TransactionFactory {
    override fun newTransaction(conn: Connection?): Transaction {
        throw UnsupportedOperationException("Need a dataSource to create transaction.")
    }

    override fun newTransaction(
        dataSource: DataSource,
        level: TransactionIsolationLevel?,
        autoCommit: Boolean
    ): Transaction {
        return KtManagedTransaction(dataSource)
    }
}