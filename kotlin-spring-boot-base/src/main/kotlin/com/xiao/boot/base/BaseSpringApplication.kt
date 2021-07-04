package com.xiao.boot.base

import org.springframework.boot.SpringApplication
import org.springframework.boot.SpringBootConfiguration

/**
 * Need to add @ComponentScan when using it.
 *
 * @author lix wang
 */
@SpringBootConfiguration
abstract class BaseSpringApplication : BaseEnableAutoConfiguration() {
    companion object {
        @JvmStatic
        fun start(clazz: Class<*>, vararg args: String) {
            SpringApplication.run(clazz, *args)
        }
    }
}