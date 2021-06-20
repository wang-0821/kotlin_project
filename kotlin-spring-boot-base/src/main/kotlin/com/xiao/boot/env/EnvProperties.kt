package com.xiao.boot.env

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class EnvProperties(
    val value: Array<EnvProperty> = []
)