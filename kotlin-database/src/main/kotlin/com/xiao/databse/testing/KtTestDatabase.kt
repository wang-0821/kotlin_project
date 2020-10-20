package com.xiao.databse.testing

import com.xiao.databse.MyBatisDatabase
import kotlin.reflect.KClass

/**
 *
 * @author lix wang
 */
@Repeatable
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class KtTestDatabase(
    val database: KClass<out MyBatisDatabase>,
    val tables: Array<String>
)