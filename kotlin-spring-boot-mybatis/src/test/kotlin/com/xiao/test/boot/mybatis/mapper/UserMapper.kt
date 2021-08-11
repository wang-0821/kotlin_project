package com.xiao.test.boot.mybatis.mapper

import com.xiao.boot.mybatis.annotation.MapperRetry
import com.xiao.boot.mybatis.testing.TestMapperTables
import com.xiao.test.boot.mybatis.model.User
import org.apache.ibatis.annotations.Param
import org.apache.ibatis.annotations.Select
import org.apache.ibatis.annotations.Update

/**
 *
 * @author lix wang
 */
@TestMapperTables(tables = ["users"])
interface UserMapper {
    @MapperRetry
    fun selectById(@Param("id") id: Long): User?

    @Select(
        """
        SELECT 
            $COLUMNS
        FROM
            $TABLE
        WHERE
            id = #{id}
        """
    )
    @MapperRetry
    fun findById(@Param("id") id: Long): User?

    @Select(
        """
        SELECT
            $COLUMNS
        FROM
            $TABLE
        INNER JOIN user_task ON user_task.user_id = $TABLE.id
        WHERE
            $TABLE.id = #{id}
        """
    )
    @MapperRetry
    fun findByIdWithJoin(@Param("id") id: Long): User?

    @Update(
        """
        UPDATE
            users
        SET
            username = #{username}
        WHERE
            id = #{id}
        """
    )
    fun updateUsernameById(@Param("id") id: Long, @Param("username") username: String)

    companion object {
        private const val TABLE = "users"
        const val COLUMNS = "$TABLE.id, $TABLE.username, $TABLE.password"
    }
}