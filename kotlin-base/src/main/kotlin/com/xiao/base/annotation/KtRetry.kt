package com.xiao.base.annotation

/**
 * 可以添加在Mapper method上，用以处理SQL执行重试。
 *
 * @author lix wang
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.ANNOTATION_CLASS)
annotation class KtRetry(
    val times: Int = 2
)