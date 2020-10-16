package com.xiao.databse

/**
 *
 * @author lix wang
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class KtDatabase(
    val name: String,
    val mapperPath: String,
    val mapperXmlPath: String,
    val dataSetPath: String
)