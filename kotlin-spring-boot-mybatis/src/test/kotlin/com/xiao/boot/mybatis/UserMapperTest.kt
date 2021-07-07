package com.xiao.boot.mybatis

import com.xiao.boot.mybatis.mapper.UserMapper
import com.xiao.boot.mybatis.properties.DemoDatabase
import com.xiao.boot.mybatis.properties.DemoDatabaseProperties
import com.xiao.boot.mybatis.testing.KtSpringMybatisTestBase
import com.xiao.boot.mybatis.testing.TestKtSpringDatabase
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
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
}