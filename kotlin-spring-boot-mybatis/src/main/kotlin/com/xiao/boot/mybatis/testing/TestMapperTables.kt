package com.xiao.boot.mybatis.testing

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class TestMapperTables(
    val tables: Array<String> = []
)