package xiao.boot.server.demo.mapper

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import xiao.boot.mybatis.testing.KtSpringMybatisTestBase
import xiao.boot.mybatis.testing.TestKtSpringDatabase
import xiao.boot.server.demo.DemoServerConfiguration
import xiao.boot.server.demo.mybatis.mapper.UserMapper
import xiao.boot.server.demo.properties.DemoDatabase

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