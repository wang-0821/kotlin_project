package com.xiao.databse

/**
 * TODO 利用注解加容器的方式，可以简化多数据源配置。
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