package com.xiao.boot.base

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

/**
 *
 * @author lix wang
 */
@SpringBootApplication
abstract class BaseSpringApplication {
    companion object {
        fun start(clazz: Class<*>, vararg args: String) {
            SpringApplication.run(clazz, *args)
        }
    }
}