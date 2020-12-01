package com.xiao.databse.testing

import com.xiao.databse.BaseDatabase
import kotlin.reflect.KClass

/**
 *
 * @author lix wang
 */
@Repeatable
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class KtTestDatabase(
    val database: KClass<out BaseDatabase>,
    val tables: Array<String>
)