package com.xiao.boot.server.demo.mybatis.mapper

import com.xiao.boot.mybatis.testing.TestMapperTables
import com.xiao.boot.server.demo.model.User
import org.apache.ibatis.annotations.Param
import org.apache.ibatis.annotations.Select
import org.apache.ibatis.annotations.Update

/**
 *
 * @author lix wang
 */
@TestMapperTables(tables = ["users"])
interface UserMapper {
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
    fun updateUsernameById(
        @Param("id") id: Long,
        @Param("username") username: String
    )

    @Select(
        """
        SELECT
            $COLUMNS
        FROM
            users
        WHERE
            id = #{id}
        """
    )
    fun selectById(@Param("id") id: Long): User

    companion object {
        private const val TABLE = "users"
        const val COLUMNS = "$TABLE.id, $TABLE.username, $TABLE.password"
    }
}