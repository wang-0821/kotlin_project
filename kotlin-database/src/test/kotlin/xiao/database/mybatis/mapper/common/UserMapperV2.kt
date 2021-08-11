package xiao.database.mybatis.mapper.common

import org.apache.ibatis.annotations.Param
import org.apache.ibatis.annotations.Select
import xiao.database.model.User
import xiao.databse.annotation.KtMapperTables
import xiao.databse.annotation.KtRetry

/**
 *
 * @author lix wang
 */
@KtMapperTables(["users"])
interface UserMapperV2 {
    @KtRetry
    @Select("SELECT * FROM users WHERE id = #{id}")
    fun getById(@Param("id") id: Long): User
}