package com.xiao.boot.server.base

import com.xiao.boot.base.BaseSpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

/**
 *
 * @author lix wang
 */
@SpringBootApplication
class ServerSpringApplication

fun main() {
    BaseSpringApplication.start(ServerSpringApplication::class.java, "demo-server")
}