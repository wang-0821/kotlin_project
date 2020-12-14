package com.xiao.databse.utils

import com.xiao.databse.TransactionHandler
import com.xiao.databse.TransactionalWrapper
import javax.sql.DataSource
import kotlin.reflect.KClass

/**
 *
 * @author lix wang
 */
object TransactionalUtils {
    private val resources = ThreadLocal<MutableMap<Any, Any>>()
    private val transactionalWrapper = ThreadLocal<TransactionalWrapper>()
    private val transactionHandlers = ThreadLocal< MutableList<TransactionHandler>>()

    @Suppress("UNCHECKED_CAST")
    fun <T> getResource(key: Any): T? {
        val transactionalWrapper = transactionalWrapper.get()
        if (transactionalWrapper != null) {
            return resources.get()?.get(key) as? T
        }
        return null
    }

    fun setResource(key: Any, resource: Any) {
        checkAndGetTransactionWrapper()
        val map = resources.get()
        if (map == null) {
            resources.set(mutableMapOf(key to resource))
        } else {
            map[key] = resource
        }
    }

    fun removeResource(key: Any) {
        checkAndGetTransactionWrapper()
        resources.get()?.remove(key)
    }

    fun isTransactional(dataSource: DataSource): Boolean {
        val transactionalWrapper = transactionalWrapper.get()
        return if (transactionalWrapper == null) {
            false
        } else {
            transactionalWrapper.dataSources.isNullOrEmpty() || transactionalWrapper.dataSources.contains(dataSource)
        }
    }

    fun setTransactionalWrapper(wrapper: TransactionalWrapper) {
        val wrappers = transactionalWrapper.get()
        if (wrappers == null) {
            transactionalWrapper.set(wrapper)
        } else {
            throw IllegalArgumentException("Can't use nested transactions.")
        }
    }

    fun checkAndGetTransactionWrapper(): TransactionalWrapper {
        return transactionalWrapper.get() ?: throw IllegalStateException("Not in transaction.")
    }

    fun registerTransactionHandler(handler: TransactionHandler) {
        checkAndGetTransactionWrapper()
        val handlers = transactionHandlers.get()
        if (handlers == null) {
            transactionHandlers.set( mutableListOf(handler))
        } else {
            handlers.add(handler)
        }
    }

    fun transactionHandlers(): List<TransactionHandler> {
        return transactionHandlers.get() ?: listOf()
    }

    fun releaseTransaction() {
        transactionalWrapper.set(null)
        resources.get()?.clear()
        transactionHandlers.get()?.clear()
    }

    fun needRollback(throwable: Throwable, rollbackFor: List<KClass<*>>, noRollbackFor: List<KClass<*>>): Boolean {
        val noRollbackResult = noRollbackFor.filter { throwable::class.java.isAssignableFrom(it::class.java) }
        val rollbackResult = rollbackFor
            .filter { rollbackEx ->
                throwable::class.java.isAssignableFrom(rollbackEx::class.java) &&
                    noRollbackResult.none {
                        rollbackEx::class.java != it::class.java
                            && rollbackEx::class.java.isAssignableFrom(it::class.java)
                    }
            }
        return (noRollbackResult.isEmpty() && rollbackResult.isEmpty()) || rollbackResult.isNotEmpty()
    }
}