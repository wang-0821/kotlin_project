package com.xiao.boot.mybatis

import com.xiao.base.testing.KtTestBase
import com.xiao.boot.mybatis.properties.DemoDatabaseProperties
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

/**
 *
 * @author lix wang
 */
@Disabled
@SpringBootTest(classes = [KtSpringMybatisAutoConfiguration::class])
class UserMapperTest : KtTestBase() {
    @Autowired
    lateinit var demoDatabaseProperties: DemoDatabaseProperties

    @Test
    fun `test get userMapper`() {
        println(demoDatabaseProperties)
        Assertions.assertEquals(demoDatabaseProperties.databaseUsername, "root")
    }
}