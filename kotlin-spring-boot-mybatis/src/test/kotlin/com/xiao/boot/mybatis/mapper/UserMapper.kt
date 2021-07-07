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
            users
        WHERE
            id = #{id}
        """
    )
    fun findById(@Param("id") id: Long): User?

    companion object {
        const val COLUMNS = "id, username, password"
    }
}