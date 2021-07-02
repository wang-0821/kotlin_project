package com.xiao.boot.mybatis

import com.xiao.base.testing.KtTestBase
import com.xiao.boot.mybatis.mapper.UserMapper
import com.xiao.boot.mybatis.properties.DemoDatabaseProperties
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.lang.reflect.Proxy

/**
 *
 * @author lix wang
 */
@SpringBootTest(classes = [KtMybatisAutoConfiguration::class])
class UserMapperTest : KtTestBase() {
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
    fun `test mapper query`() {

    }
}