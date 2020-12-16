package com.xiao.databse.annotation

/**
 *
 * @author lix wang
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class KtMapperTables(
    val value: Array<String> = []
)