package com.xiao.boot.server.base.annotations

import org.springframework.context.annotation.Import

/**
 *
 * @author lix wang
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.ANNOTATION_CLASS)
@Import(ServletCustomExecutorRegistrar::class)
annotation class EnableCustomExecutor