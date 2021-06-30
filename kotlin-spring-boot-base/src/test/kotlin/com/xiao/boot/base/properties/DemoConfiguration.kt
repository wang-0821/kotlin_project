package com.xiao.boot.base.properties

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 *
 * @author lix wang
 */
@Configuration
class DemoConfiguration {
    @Bean
    fun createClassA(): ClassA {
        return ClassA(createClassB())
    }

    @Bean
    fun createClassB(): ClassB {
        return ClassB()
    }
}