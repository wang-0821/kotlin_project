package xiao.boot.server.demo.service

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import xiao.boot.mybatis.tx.TransactionService
import xiao.boot.server.demo.mybatis.mapper.UserMapper
import xiao.boot.server.demo.properties.DemoDatabase

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