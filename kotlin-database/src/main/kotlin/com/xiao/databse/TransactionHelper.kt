package com.xiao.databse

import com.xiao.base.logging.KtLogger
import com.xiao.base.logging.LoggerType
import com.xiao.base.logging.Logging
import com.xiao.databse.utils.TransactionalUtils

/**
 *
 * @author lix wang
 */
@KtLogger(LoggerType.MAPPER)
object TransactionHelper : Logging() {
    fun doInTransaction(action: () -> Unit, wrapper: TransactionalWrapper? = null) {
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
            TransactionalUtils.releaseTransaction()
        }
    }

    private fun processCommit() {
        for (handler in TransactionalUtils.transactionHandlers()) {
            handler.beforeCommit()
        }
        for (handler in TransactionalUtils.transactionHandlers()) {
            handler.commit()
        }
        for (handler in TransactionalUtils.transactionHandlers()) {
            handler.afterCommit()
        }
    }
}