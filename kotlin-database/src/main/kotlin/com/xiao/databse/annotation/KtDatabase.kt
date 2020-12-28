package com.xiao.databse.annotation

/**
 * 利用注解简化数据源配置。
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