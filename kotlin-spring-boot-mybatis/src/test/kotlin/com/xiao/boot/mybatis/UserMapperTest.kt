package com.xiao.boot.mybatis

import com.xiao.base.testing.KtTestBase
import com.xiao.boot.mybatis.mapper.UserMapper
import org.apache.ibatis.binding.MapperProxy
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

/**
 *
 * @author lix wang
 */
@SpringBootTest(classes = [KtSpringMybatisAutoConfiguration::class])
class UserMapperTest : KtTestBase() {
    @Test
    fun `test get userMapper`() {
    }
}