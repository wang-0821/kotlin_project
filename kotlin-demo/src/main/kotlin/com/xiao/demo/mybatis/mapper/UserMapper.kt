package com.xiao.demo.mybatis.mapper

import com.xiao.demo.model.User
import org.apache.ibatis.annotations.Param

/**
 *
 * @author lix wang
 */
interface UserMapper {
    fun getById(@Param("id") id: Long): User

    fun updatePasswordById(@Param("id") id: Long, @Param("password") password: String)
}