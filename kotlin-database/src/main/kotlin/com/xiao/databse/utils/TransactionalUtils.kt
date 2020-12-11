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
    private val resources = ThreadLocal<MutableMap<TransactionalWrapper, MutableMap<Any, Any>>>()
    private val transactionalWrappers = ThreadLocal<MutableList<TransactionalWrapper>>()
    private val transactionHandlers = ThreadLocal<MutableMap<TransactionalWrapper, MutableList<TransactionHandler>>>()

    @Suppress("UNCHECKED_CAST")
    fun <T> getResource(key: Any): T? {
        return resources.get()?.get(checkAndGetTransactionWrapper())?.get(key) as? T
    }

    fun setResource(key: Any, resource: Any) {
        val transactionalWrapper = checkAndGetTransactionWrapper()
        val map = resources.get()
        if (map == null) {
            resources.set(mutableMapOf(transactionalWrapper to mutableMapOf(key to resource)))
        } else {
            val transactionalResourceMap = map[transactionalWrapper]
            if (transactionalResourceMap == null) {
                map[transactionalWrapper] = mutableMapOf(key to resource)
            } else {
                transactionalResourceMap[key] = resource
            }
        }
    }

    fun removeResource(key: Any) {
        resources.get()?.get(checkAndGetTransactionWrapper())?.remove(key)
    }

    fun isTransactional(dataSource: DataSource): Boolean {
        val transactionalWrapper = transactionalWrappers.get()?.firstOrNull()
        return if (transactionalWrapper == null) {
            false
        } else {
            transactionalWrapper.dataSources.isNullOrEmpty() || transactionalWrapper.dataSources.contains(dataSource)
        }
    }

    fun setTransactionalWrapper(wrapper: TransactionalWrapper) {
        val wrappers = transactionalWrappers.get()
        if (wrappers == null) {
            transactionalWrappers.set(mutableListOf(wrapper))
        } else {
            wrappers.add(0, wrapper)
        }
    }

    fun checkAndGetTransactionWrapper(): TransactionalWrapper {
        return transactionalWrappers.get()?.firstOrNull() ?: throw IllegalStateException("Not in transaction.")
    }

    fun registerTransactionHandler(handler: TransactionHandler) {
        val transactionalWrapper = checkAndGetTransactionWrapper()
        val handlerMap = transactionHandlers.get()
        if (handlerMap == null) {
            transactionHandlers.set(mutableMapOf(transactionalWrapper to mutableListOf(handler)))
        } else {
            val transactionalHandlers = handlerMap[transactionalWrapper]
            if (transactionalHandlers == null) {
                handlerMap[transactionalWrapper] = mutableListOf(handler)
            } else {
                transactionalHandlers.add(handler)
            }
        }
    }

    fun transactionHandlers(): List<TransactionHandler> {
        return transactionHandlers.get()?.get(checkAndGetTransactionWrapper()) ?: listOf()
    }

    fun releaseTransaction() {
        val transactionalWrapper = transactionalWrappers.get()?.removeAt(0)
        resources.get()?.remove(transactionalWrapper)
        transactionHandlers.get()?.remove(transactionalWrapper)
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