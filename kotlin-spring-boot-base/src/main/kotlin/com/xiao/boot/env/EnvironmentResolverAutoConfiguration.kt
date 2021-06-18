package com.xiao.boot.env

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 *
 * @author lix wang
 */
@Configuration
class EnvironmentResolverAutoConfiguration {
    @Bean
    fun envConstants(): EnvInfoProvider {
        TODO()
    }
}