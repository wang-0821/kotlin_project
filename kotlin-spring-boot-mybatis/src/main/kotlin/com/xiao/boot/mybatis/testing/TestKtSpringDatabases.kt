package com.xiao.boot.mybatis.testing

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class TestKtSpringDatabases(
    vararg val databases: TestKtSpringDatabase = []
)