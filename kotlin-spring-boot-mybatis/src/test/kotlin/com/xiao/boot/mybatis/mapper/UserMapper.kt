package com.xiao.boot.mybatis.mapper

import com.xiao.boot.mybatis.annotation.MapperRetry
import com.xiao.boot.mybatis.model.User
import com.xiao.boot.mybatis.testing.TestMapperTables
import org.apache.ibatis.annotations.Param
import org.apache.ibatis.annotations.Select

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

    companion object {
        private const val TABLE = "users"
        const val COLUMNS = "$TABLE.id, $TABLE.username, $TABLE.password"
    }
}