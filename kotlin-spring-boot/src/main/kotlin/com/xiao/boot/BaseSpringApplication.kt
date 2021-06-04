package com.xiao.boot

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

/**
 *
 * @author lix wang
 */
@SpringBootApplication
abstract class BaseSpringApplication {
    companion object {
        @JvmStatic
        fun start(clazz: Class<*>, vararg args: String) {
            // Set text banner location, default location is "banner.txt".
            System.setProperty("spring.banner.location", "lix-banner.txt")
            SpringApplication.run(clazz, *args)
        }
    }
}