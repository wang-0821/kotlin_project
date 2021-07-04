package com.xiao.boot.mybatis.mapper

import com.xiao.boot.mybatis.annotation.MapperRetry
import com.xiao.boot.mybatis.model.User
import org.apache.ibatis.annotations.Param

/**
 *
 * @author lix wang
 */
interface UserMapper {
    @MapperRetry
    fun selectById(@Param("id") id: Long): User?
}