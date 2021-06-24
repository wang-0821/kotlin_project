package com.xiao.boot.base.property

import com.xiao.boot.base.env.EnvConstants.ENV_ENCRYPT_KEY
import com.xiao.boot.base.env.ProfileType

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