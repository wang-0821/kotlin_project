package com.xiao.database.mybatis.mapper.common

import com.xiao.database.model.User
import com.xiao.databse.annotation.KtMapperRetry
import com.xiao.databse.annotation.KtMapperTables
import org.apache.ibatis.annotations.Param
import org.apache.ibatis.annotations.Select

/**
 *
 * @author lix wang
 */
@KtMapperTables(["users"])
interface UserMapperV2 {
    @KtMapperRetry
    @Select("SELECT * FROM users WHERE id = #{id}")
    fun getById(@Param("id") id: Long): User
}