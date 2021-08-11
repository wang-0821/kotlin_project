package com.xiao.test.boot.mybatis.transaction

import com.xiao.boot.mybatis.testing.KtSpringMybatisTestBase
import com.xiao.boot.mybatis.testing.TestKtSpringDatabase
import com.xiao.boot.mybatis.tx.TransactionService
import com.xiao.test.boot.mybatis.KtMybatisAutoConfiguration
import com.xiao.test.boot.mybatis.mapper.UserMapper
import com.xiao.test.boot.mybatis.properties.DemoDatabase
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest

/**
 *
 * @author lix wang
 */
@TestKtSpringDatabase(
    database = DemoDatabase::class,
    mappers = [UserMapper::class]
)
@SpringBootTest(classes = [KtMybatisAutoConfiguration::class])
class TransactionTest : KtSpringMybatisTestBase() {
    @Qualifier(DemoDatabase.transactionServiceName)
    lateinit var transactionService: TransactionService
    @Autowired
    lateinit var userMapper: UserMapper

    @Test
    fun `test transaction rollback`() {
        assertThrows<RuntimeException> {
            transactionService.runInTransaction {
                Assertions.assertEquals(userMapper.findById(1), "user_1")
                userMapper.updateUsernameById(1, "user_temp")
                Assertions.assertEquals(userMapper.findById(1)!!.username, "user_temp")
                throw RuntimeException("throw exception")
            }
        }
        Assertions.assertEquals(userMapper.findById(1)!!.username, "user_1")
    }
}