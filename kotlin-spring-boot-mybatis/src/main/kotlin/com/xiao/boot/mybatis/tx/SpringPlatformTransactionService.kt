package com.xiao.boot.mybatis.tx

import com.xiao.base.logging.Logging
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionStatus
import org.springframework.transaction.TransactionSystemException
import org.springframework.transaction.interceptor.NoRollbackRuleAttribute
import org.springframework.transaction.interceptor.RollbackRuleAttribute
import org.springframework.transaction.interceptor.RuleBasedTransactionAttribute
import org.springframework.transaction.interceptor.TransactionAttribute

/**
 *
 * @author lix wang
 */
class SpringPlatformTransactionService(
    private val transactionManager: PlatformTransactionManager
) : TransactionService {
    override fun <T> runInTransaction(block: () -> T): T {
        return runInTransaction(DEFAULT_TRANSACTION_WRAPPER, block)
    }

    override fun <T> runInTransaction(wrapper: TransactionWrapper, block: () -> T): T {
        // create transaction attributes
        val transactionAttribute = RuleBasedTransactionAttribute()
            .apply {
                propagationBehavior = wrapper.propagation.value()
                isolationLevel = wrapper.isolation.value()
                timeout = wrapper.timeout
                isReadOnly = wrapper.readOnly
                qualifier = wrapper.value
                rollbackRules = wrapper.rollbackFor
                    .map {
                        RollbackRuleAttribute(it)
                    } + wrapper.noRollbackFor
                    .map {
                        NoRollbackRuleAttribute(it)
                    }
            }
        val transactionStatus = transactionManager.getTransaction(transactionAttribute)
        val value: T
        try {
            value = block()
        } catch (ex: Throwable) {
            completeTransactionAfterThrowing(transactionStatus, transactionAttribute, transactionManager, ex)
            throw ex
        }
        transactionManager.commit(transactionStatus)
        return value
    }

    private fun completeTransactionAfterThrowing(
        transactionStatus: TransactionStatus,
        transactionAttribute: TransactionAttribute,
        transactionManager: PlatformTransactionManager,
        throwable: Throwable
    ) {
        if (transactionAttribute.rollbackOn(throwable)) {
            try {
                transactionManager.rollback(transactionStatus)
            } catch (ex: Throwable) {
                when (ex) {
                    is TransactionSystemException -> {
                        log.error("Application exception overridden by rollback exception", throwable)
                        ex.initApplicationException(throwable)
                        throw ex
                    }
                    is RuntimeException,
                    is Error -> {
                        log.error("Application exception overridden by rollback exception", throwable)
                        throw ex
                    }
                }
            }
        }
    }

    companion object : Logging() {
        val DEFAULT_TRANSACTION_WRAPPER = TransactionWrapper()
    }
}