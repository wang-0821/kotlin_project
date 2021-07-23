package com.xiao.boot.base

import com.xiao.boot.base.ServerConstants.SERVER_NAME_KEY
import org.springframework.boot.SpringApplication

/**
 *
 * @author lix wang
 */
abstract class BaseSpringApplication {
    companion object {
        @JvmStatic
        fun start(cls: Class<*>, serverName: String, vararg args: String) {
            System.setProperty(SERVER_NAME_KEY, serverName)
            SpringApplication.run(cls, *args)
        }
    }
}