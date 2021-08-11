package xiao.database.mybatis.mapper.common

import org.apache.ibatis.annotations.Param
import xiao.database.model.User
import xiao.databse.annotation.KtMapperTables
import xiao.databse.annotation.KtRetry

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