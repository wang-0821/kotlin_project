package com.xiao.boot.base

import org.springframework.boot.SpringApplication
import org.springframework.boot.test.context.SpringBootContextLoader
import org.springframework.context.annotation.ComponentScan

/**
 * We don't annotate this class with [@Component],
 * because this class will set as [SpringApplication]'s primarySources
 * while [SpringBootContextLoader] loadContext.
 *
 * When [SpringApplication] prepareContext will register primarySources as BeanDefinitions
 *
 * @author lix wang
 */
@ComponentScan
class KtSpringBootBaseAutoConfiguration : BaseEnableAutoConfiguration()