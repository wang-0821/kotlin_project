package com.xiao.databse.annotation

/**
 *
 * @author lix wang
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class KtTestDatabases(
    vararg val value: KtTestDatabase = []
)