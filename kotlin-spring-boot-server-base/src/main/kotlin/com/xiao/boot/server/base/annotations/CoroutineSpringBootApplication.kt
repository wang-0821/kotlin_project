package com.xiao.boot.server.base.annotations

import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.ComponentScan
import java.lang.annotation.Inherited

/**
 *
 * @author lix wang
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@Inherited
@MustBeDocumented
@ComponentScan
@SpringBootConfiguration
@EnableAutoConfiguration
@EnableCoroutineDispatcher
annotation class CoroutineSpringBootApplication