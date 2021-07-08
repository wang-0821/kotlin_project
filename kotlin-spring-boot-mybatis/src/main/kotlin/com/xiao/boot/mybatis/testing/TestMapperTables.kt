package com.xiao.boot.mybatis.testing

/**
 * Specify tables to migrate in testing environment.
 *
 * @author lix wang
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class TestMapperTables(
    val tables: Array<String> = []
)