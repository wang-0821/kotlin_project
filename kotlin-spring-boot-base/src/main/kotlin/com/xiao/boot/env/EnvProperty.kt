package com.xiao.boot.env

/**
 *
 * @author lix wang
 */
@Repeatable
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class EnvProperty(
    val profiles: Array<ProfileType> = [],
    val value: String = "",
    val encrypt: Boolean = false,
    val allowEmpty: Boolean = false
)