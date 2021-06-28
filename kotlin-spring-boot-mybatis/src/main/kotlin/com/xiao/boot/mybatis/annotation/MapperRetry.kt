package com.xiao.boot.mybatis.annotation

/**
 *
 * @author lix wang
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class MapperRetry(
    val times: Int = 2
)