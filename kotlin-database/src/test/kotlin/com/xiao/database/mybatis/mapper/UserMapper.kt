package com.xiao.database.mybatis.mapper

import com.xiao.database.model.User
import com.xiao.databse.annotation.KtMapperRetry
import com.xiao.databse.annotation.KtMapperTables
import org.apache.ibatis.annotations.Param

/**
 *
 * @author lix wang
 */
@KtMapperTables(["users"])
interface UserMapper {
    @KtMapperRetry
    fun getById(@Param("id") id: Long): User

    @KtMapperRetry
    fun updatePasswordById(@Param("id") id: Long, @Param("password") password: String)
}