package com.xiao.database

import com.xiao.database.database.DemoDatabase
import com.xiao.database.mybatis.mapper.UserMapper
import com.xiao.database.mybatis.mapper.UserMapperV2
import com.xiao.databse.TransactionHelper
import com.xiao.databse.annotation.KtTestDatabase
import com.xiao.databse.annotation.KtTestDatabases
import com.xiao.databse.testing.KtTestDataSourceBase
import com.xiao.databse.utils.MapperUtils
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

/**
 *
 * @author lix wang
 */
@KtTestDatabases([
    KtTestDatabase(
        database = DemoDatabase::class,
        mappers = [
            UserMapper::class,
            UserMapperV2::class
        ]
    )
])
class MyBatisTestMapperTest : KtTestDataSourceBase() {
    @Test
    fun `test query using custom testMapperProxy with different sqlSessions`() {
        val userMapper = MapperUtils.getTestMapper(UserMapper::class.java)
        val userMapperV2 = MapperUtils.getTestMapper(UserMapperV2::class.java)

        Assertions.assertEquals(userMapper.getById(1L).username, "user_1")
        userMapper.updatePasswordById(1L, "password_temp")
        Assertions.assertEquals(userMapperV2.getById(1L).password, "password_temp")
    }

    @Test
    fun `test rollback with transaction`() {
        val userMapper = MapperUtils.getTestMapper(UserMapper::class.java)
        Assertions.assertEquals(userMapper.getById(1L).password, "password_1")
        val exception = assertThrows<IllegalStateException> {
            TransactionHelper.doInTransaction {
                Assertions.assertEquals(userMapper.getById(1L).password, "password_1")
                userMapper.updatePasswordById(1L, "password_temp")
                Assertions.assertEquals(userMapper.getById(1L).password, "password_temp")
                throw IllegalStateException("throws exception.")
            }
        }
        Assertions.assertEquals("throws exception.", exception.message)
        Assertions.assertEquals(userMapper.getById(1L).password, "password_1")
    }

    @Test
    fun `test commit with transaction`() {
        val userMapper = MapperUtils.getTestMapper(UserMapper::class.java)
        Assertions.assertEquals(userMapper.getById(1L).password, "password_1")
        TransactionHelper.doInTransaction {
            Assertions.assertEquals(userMapper.getById(1L).password, "password_1")
            userMapper.updatePasswordById(1L, "password_temp")
            Assertions.assertEquals(userMapper.getById(1L).password, "password_temp")
        }
        Assertions.assertEquals(userMapper.getById(1L).password, "password_temp")
    }
}