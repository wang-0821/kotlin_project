package com.xiao.database

import com.xiao.database.database.DemoDatabase
import com.xiao.database.mybatis.mapper.common.UserMapper
import com.xiao.database.mybatis.mapper.common.UserMapperV2
import com.xiao.databse.annotation.KtTestDatabase
import com.xiao.databse.testing.KtTestDataSourceBase
import com.xiao.databse.testing.TestResourceHolder
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
    mappers = [UserMapper::class, UserMapperV2::class]
)
class MyBatisMapperTest : KtTestDataSourceBase() {
    private lateinit var sqlSessionFactory: SqlSessionFactory

    @BeforeAll
    fun init() {
        sqlSessionFactory = TestResourceHolder.getDatabase(DemoDatabase::class).sqlSessionFactory()
    }

    @Test
    fun `test mapper query with same sqlSession without cache`() {
        val sqlSession = sqlSessionFactory.openSession()
        val userMapper = sqlSessionFactory.configuration.getMapper(UserMapper::class.java, sqlSession)
        val userMapperV2 = sqlSessionFactory.configuration.getMapper(UserMapperV2::class.java, sqlSession)

        assertEquals(userMapper.getById(1L).username, "user_1")
        userMapper.updatePasswordById(1L, "password_temp")
        assertEquals(userMapperV2.getById(1L).password, "password_temp")
    }

    @Test
    fun `test mapper query exception without transaction`() {
        val sqlSession = sqlSessionFactory.openSession()
        val userMapper = sqlSessionFactory.configuration.getMapper(UserMapper::class.java, sqlSession)

        assertEquals(userMapper.getById(1L).password, "password_1")
        val exception = assertThrows<IllegalStateException> {
            userMapper.updatePasswordById(1L, "password_temp")
            throw IllegalStateException("throws exception.")
        }
        assertEquals("throws exception.", exception.message)
        assertEquals(userMapper.getById(1L).password, "password_temp")
    }
}