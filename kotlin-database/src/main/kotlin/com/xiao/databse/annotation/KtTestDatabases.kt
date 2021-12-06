package com.xiao.databse.annotation

import com.xiao.database.annotation.KtTestDatabase

/**
 *
 * @author lix wang
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class KtTestDatabases(
    vararg val value: KtTestDatabase = []
)