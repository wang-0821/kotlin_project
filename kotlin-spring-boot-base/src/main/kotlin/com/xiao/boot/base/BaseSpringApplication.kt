package com.xiao.boot.base

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

/**
 *
 * @author lix wang
 */
@SpringBootApplication
abstract class BaseSpringApplication {
    fun init() {
        // Set text banner location, default location is "banner.txt".
        System.setProperty("spring.banner.location", "lix-banner.txt")
    }

    fun start(clazz: Class<*>, vararg args: String) {
        init()
        SpringApplication.run(clazz, *args)
    }
}