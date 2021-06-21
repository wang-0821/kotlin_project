package com.xiao.boot.base.env

import com.xiao.boot.base.env.EnvConstants.ENV_ENCRYPT_KEY

/**
 * Target class fileds must be public.
 *
 * @author lix wang
 */
@Repeatable
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class EnvProperty(
    val profiles: Array<ProfileType> = [],
    val value: String = "",
    val allowEmpty: Boolean = false,
    val encrypt: Boolean = false,
    val encryptKey: String = ENV_ENCRYPT_KEY
)