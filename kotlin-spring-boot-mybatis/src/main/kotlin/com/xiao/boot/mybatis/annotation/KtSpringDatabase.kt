package com.xiao.boot.mybatis.annotation

import org.springframework.context.annotation.Import

/**
 *
 * @author lix wang
 */

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@Import(KtSpringDatabaseRegistrar::class)
annotation class KtSpringDatabase(
    val name: String,
    val mapperBasePackage: String = "",
    val mapperXmlPattern: String = "",
    val dataScriptPattern: String = ""
)