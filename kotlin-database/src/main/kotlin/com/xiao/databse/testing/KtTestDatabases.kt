package com.xiao.databse.testing

/**
 *
 * @author lix wang
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class KtTestDatabases(
    val value: Array<KtTestDatabase>
)