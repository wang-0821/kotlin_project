package com.xiao.test.boot.mybatis.mapper

import com.xiao.boot.mybatis.testing.KtSpringMybatisTestBase
import com.xiao.boot.mybatis.testing.TestKtSpringDatabase
import com.xiao.test.boot.mybatis.KtMybatisAutoConfiguration
import com.xiao.test.boot.mybatis.properties.DemoDatabase
import com.xiao.test.boot.mybatis.properties.DemoDatabaseProperties
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mybatis.spring.MyBatisSystemException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.lang.reflect.Proxy

/**
 *
 * @author lix wang
 */
@TestKtSpringDatabase(
    database = DemoDatabase::class,
    mappers = [UserMapper::class]
)
@SpringBootTest(classes = [KtMybatisAutoConfiguration::class])
class UserMapperTest : KtSpringMybatisTestBase() {
    @Autowired
    lateinit var demoDatabaseProperties: DemoDatabaseProperties

    @Autowired
    lateinit var userMapper: UserMapper

    @Test
    fun `test get userMapper`() {
        Assertions.assertEquals(demoDatabaseProperties.databaseUsername, "root")
        Assertions.assertTrue(Proxy.isProxyClass(userMapper::class.java))
    }

    @Test
    fun `test query user by id`() {
        Assertions.assertEquals(userMapper.selectById(1)!!.username, "user_1")
    }

    @Test
    fun `test query user by id with annotation`() {
        Assertions.assertEquals(userMapper.findById(1)!!.username, "user_1")
    }

    @Test
    fun `test query user by id lack of table migrated`() {
        val ex = assertThrows<MyBatisSystemException> {
            userMapper.findByIdWithJoin(1)
        }
        Assertions.assertEquals(ex.cause!!.message, "Forget to migrate tables: user_task for database: demoDatabase.")
    }
}