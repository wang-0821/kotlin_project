package com.xiao.database.mybatis.mapper

import com.xiao.database.model.User
import org.apache.ibatis.annotations.Param
import org.apache.ibatis.annotations.Select

/**
 *
 * @author lix wang
 */
interface UserMapperV2 {
    @Select("SELECT * FROM users WHERE id = #{id}")
    fun getById(@Param("id") id: Long): User
}