package com.xiao.databse

import com.xiao.base.logging.KtLogger
import com.xiao.base.logging.LoggerType
import com.xiao.base.logging.Logging
import com.xiao.databse.utils.TransactionalUtils

/**
 *
 * @author lix wang
 */
@KtLogger(LoggerType.DATA_SOURCE)
object TransactionHelper : Logging() {
    fun doInTransaction(wrapper: TransactionalWrapper? = null, action: () -> Unit) {
        try {
            TransactionalUtils.setTransactionalWrapper(wrapper ?: TransactionalWrapper())
            action()
            processCommit()
        } catch (throwable: Throwable) {
            for (handler in TransactionalUtils.transactionHandlers()) {
                handler.rollback(throwable)
            }
            log.error("Transaction call failed. ${throwable.message}", throwable)
            throw throwable
        } finally {
            for (handler in TransactionalUtils.transactionHandlers()) {
                handler.afterTransaction()
            }
            TransactionalUtils.releaseTransaction()
        }
    }

    private fun processCommit() {
        for (handler in TransactionalUtils.transactionHandlers()) {
            handler.beforeTransaction()
        }
        for (handler in TransactionalUtils.transactionHandlers()) {
            handler.commit()
        }
    }
}