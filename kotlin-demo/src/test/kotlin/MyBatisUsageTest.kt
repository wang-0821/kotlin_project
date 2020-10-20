import com.xiao.databse.TransactionHelper
import com.xiao.databse.testing.KtDataSourceTestBase
import com.xiao.databse.testing.KtTestDatabase
import com.xiao.demo.mybatis.DemoDatabase
import com.xiao.demo.mybatis.mapper.UserMapper
import com.xiao.demo.mybatis.mapper.UserMapperV2
import org.apache.ibatis.session.SqlSessionFactory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

/**
 *
 * @author lix wang
 */
@KtTestDatabase(
    database = DemoDatabase::class,
    tables = ["users"]
)
class MyBatisUsageTest : KtDataSourceTestBase() {
    private lateinit var sqlSessionFactory: SqlSessionFactory

    @BeforeAll
    fun init() {
        sqlSessionFactory = database(DemoDatabase::class).sqlSessionFactory()
    }

    @Test
    fun `test mapper query`() {
        val sqlSession = sqlSessionFactory.openSession()
        val userMapper = sqlSessionFactory.configuration.getMapper(UserMapper::class.java, sqlSession)
        val userMapperV2 = sqlSessionFactory.configuration.getMapper(UserMapperV2::class.java, sqlSession)

        assertEquals(userMapper.getById(1).username, "user_1")
        userMapper.updatePasswordById(1, "password_temp")
        assertEquals(userMapperV2.getById(1).password, "password_temp")
    }

    @Test
    fun `test transaction fallback`() {
        val sqlSession = sqlSessionFactory.openSession()
        val userMapper = sqlSessionFactory.configuration.getMapper(UserMapper::class.java, sqlSession)

        assertEquals(userMapper.getById(1).password, "password_1")
        val exception = assertThrows<IllegalStateException> {
            TransactionHelper(sqlSession).doInTransaction {
                userMapper.updatePasswordById(1, "password_temp")
                throw IllegalStateException("throws exception.")
            }
        }
        assertEquals("throws exception.", exception.message)
        assertEquals(userMapper.getById(1).password, "password_1")
    }
}