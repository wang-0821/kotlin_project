package com.xiao.boot.mybatis.testing

/**
 *
 * @author lix wang
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class TestKtSpringDatabases(
    vararg val value: TestKtSpringDatabase = []
)