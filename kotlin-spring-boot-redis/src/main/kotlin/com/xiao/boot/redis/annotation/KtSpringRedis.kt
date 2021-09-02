package com.xiao.boot.redis.annotation

import org.springframework.context.annotation.Import
import java.lang.annotation.Inherited

/**
 *
 * @author lix wang
 */
@Inherited
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@Import(RedisBeanRegistrar::class)
annotation class KtSpringRedis(
    val name: String,
    val mode: RedisClientMode = RedisClientMode.DEFAULT
)