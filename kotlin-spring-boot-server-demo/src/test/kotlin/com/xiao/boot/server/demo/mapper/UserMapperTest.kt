package com.xiao.boot.server.demo.mapper

import com.xiao.boot.mybatis.testing.KtSpringMybatisTestBase
import com.xiao.boot.mybatis.testing.TestKtSpringDatabase
import com.xiao.boot.server.demo.DemoServerConfiguration
import com.xiao.boot.server.demo.mybatis.mapper.UserMapper
import com.xiao.boot.server.demo.properties.DemoDatabase
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

/**
 *
 * @author lix wang
 */
@TestKtSpringDatabase(
    database = DemoDatabase::class,
    mappers = [UserMapper::class]
)
@SpringBootTest(classes = [DemoServerConfiguration::class])
class UserMapperTest : KtSpringMybatisTestBase() {
    @Autowired
    lateinit var userMapper: UserMapper

    @Test
    fun `test get user by id`() {
        Assertions.assertEquals(userMapper.selectById(1).username, "user_1")
    }
}