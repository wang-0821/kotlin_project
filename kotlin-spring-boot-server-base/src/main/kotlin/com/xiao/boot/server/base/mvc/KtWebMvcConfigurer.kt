package com.xiao.boot.server.base.mvc

import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

/**
 *
 * @author lix wang
 */
@Component
class KtWebMvcConfigurer : WebMvcConfigurer, ApplicationContextAware {
    private lateinit var context: ApplicationContext

    override fun addInterceptors(registry: InterceptorRegistry) {
        context.getBeansOfType(KtHandlerInterceptor::class.java).values
            .forEach {
                registry.addInterceptor(it)
            }
    }

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        context = applicationContext
    }
}