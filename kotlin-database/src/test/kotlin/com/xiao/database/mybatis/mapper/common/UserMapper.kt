package com.xiao.database.mybatis.mapper.common

import com.xiao.base.annotation.KtRetry
import com.xiao.database.model.User
import com.xiao.databse.annotation.KtMapperTables
import org.apache.ibatis.annotations.Param

/**
 *
 * @author lix wang
 */
@KtMapperTables(["users"])
interface UserMapper {
    @KtRetry
    fun getById(@Param("id") id: Long): User

    @KtRetry
    fun updatePasswordById(@Param("id") id: Long, @Param("password") password: String)
}