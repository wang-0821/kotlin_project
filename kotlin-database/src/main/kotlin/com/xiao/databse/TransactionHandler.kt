package com.xiao.databse

/**
 *
 * @author lix wang
 */
interface TransactionHandler {
    fun beforeCommit()

    fun commit()

    fun afterCommit()

    fun rollback(throwable: Throwable)
}