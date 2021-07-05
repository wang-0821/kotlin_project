package com.xiao.boot.mybatis.testing

import com.xiao.boot.mybatis.database.BaseDatabase
import kotlin.reflect.KClass

/**
 *
 * @author lix wang
 */
@Repeatable
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class TestKtSpringDatabase(
    val database: KClass<out BaseDatabase>,
    val mappers: Array<KClass<*>> = []
)