package com.xiao.boot.mybatis.tx

/**
 *
 * @author lix wang
 */
interface TransactionService {
    fun <T> runInTransaction(block: () -> T): T
    fun <T> runInTransaction(wrapper: TransactionWrapper, block: () -> T): T
}