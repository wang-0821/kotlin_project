package com.xiao.databse.testing

/**
 *
 * @author lix wang
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class KtSqlTables(
    val value: Array<String> = []
)