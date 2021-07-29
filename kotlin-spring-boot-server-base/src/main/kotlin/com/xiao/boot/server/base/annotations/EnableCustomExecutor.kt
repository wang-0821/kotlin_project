package com.xiao.boot.server.base.annotations

import org.springframework.context.annotation.Import

/**
 *
 * @author lix wang
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.ANNOTATION_CLASS)
@Import(ServletExecutorRegistrar::class)
annotation class EnableCustomExecutor