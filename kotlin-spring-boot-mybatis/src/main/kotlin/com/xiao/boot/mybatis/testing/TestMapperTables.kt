package com.xiao.boot.mybatis.testing

/**
 *
 * @author lix wang
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class TestMapperTables(
    val tables: Array<String> = []
)