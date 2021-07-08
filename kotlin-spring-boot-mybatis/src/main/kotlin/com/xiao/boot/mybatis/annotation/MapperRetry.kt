package com.xiao.boot.mybatis.annotation

/**
 * Available except test active profile.
 *
 * @author lix wang
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class MapperRetry(
    val times: Int = 2
)