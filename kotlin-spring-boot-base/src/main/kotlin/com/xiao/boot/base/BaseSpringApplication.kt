package com.xiao.boot.base

import com.xiao.boot.base.ServerConstants.SERVER_NAME_KEY
import org.springframework.boot.SpringApplication

/**
 *
 * @author lix wang
 */
abstract class BaseSpringApplication(private val serverName: String) {
    fun start(vararg args: String) {
        System.setProperty(SERVER_NAME_KEY, serverName)
        SpringApplication.run(this::class.java, *args)
    }
}