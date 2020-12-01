package com.xiao.databse.utils

import com.xiao.databse.TransactionHandler
import com.xiao.databse.TransactionalWrapper
import javax.sql.DataSource

/**
 *
 * @author lix wang
 */
object TransactionalUtils {
    private val resources = ThreadLocal<MutableMap<Any, Any>>()
    private val transactionalWrapper = ThreadLocal<TransactionalWrapper>()
    private val transactionHandlers = ThreadLocal<MutableList<TransactionHandler>>()

    @Suppress("UNCHECKED_CAST")
    fun <T> getResource(key: Any): T? {
        return resources.get()?.get(key) as? T
    }

    fun setResource(key: Any, resource: Any) {
        val map = resources.get()
        if (map == null) {
            resources.set(mutableMapOf(key to resource))
        } else {
            map[key] = resource
        }
    }

    fun removeResource(key: Any) {
        val map = resources.get()
        if (map == null) {
            return
        } else {
            map.remove(key)
        }
    }

    fun isTransactional(dataSource: DataSource): Boolean {
        val transactionalWrapper = transactionalWrapper.get()
        return if (transactionalWrapper?.dataSources.isNullOrEmpty()) {
            true
        } else {
            transactionalWrapper!!.dataSources.contains(dataSource)
        }
    }

    fun setTransactionalWrapper(wrapper: TransactionalWrapper) {
        transactionalWrapper.set(wrapper)
    }

    fun getTransactionalWrapper(): TransactionalWrapper? {
        return transactionalWrapper.get()
    }

    fun registerTransactionHandler(handler: TransactionHandler) {
        val handlers = transactionHandlers.get()
        if (handlers == null) {
            transactionHandlers.set(mutableListOf(handler))
        } else {
            handlers.add(handler)
        }
    }

    fun transactionHandlers(): List<TransactionHandler> {
        return transactionHandlers.get() ?: listOf()
    }

    fun releaseTransaction() {
        transactionHandlers.remove()
        resources.remove()
        transactionalWrapper.remove()
    }
}