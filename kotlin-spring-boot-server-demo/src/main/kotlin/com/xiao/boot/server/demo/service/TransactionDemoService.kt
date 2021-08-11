package com.xiao.boot.server.demo.service

import com.xiao.boot.mybatis.tx.TransactionService
import com.xiao.boot.server.demo.mybatis.mapper.UserMapper
import com.xiao.boot.server.demo.properties.DemoDatabase
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 *
 * @author lix wang
 */
@Service
class TransactionDemoService(
    private val userMapper: UserMapper,
    @Qualifier(DemoDatabase.transactionServiceName)
    private val transactionService: TransactionService
) {
    @Transactional(rollbackFor = [Exception::class])
    fun updateUsernameInTransaction(id: Long, username: String) {
        userMapper.updateUsernameById(id, username)
        throw RuntimeException("throw exception")
    }

    fun rollbackOnTransactionService(id: Long, username: String) {
        transactionService.runInTransaction {
            userMapper.updateUsernameById(id, username)
            throw RuntimeException("throw exception")
        }
    }
}