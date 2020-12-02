package com.xiao.databse

/**
 *
 * @author lix wang
 */
interface TransactionAction<T> {
    fun doInTransaction(transactionalWrapper: TransactionalWrapper?): T?
}