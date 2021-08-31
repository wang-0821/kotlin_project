package com.xiao.boot.admin.server

import com.xiao.boot.base.BaseSpringApplication
import de.codecentric.boot.admin.server.config.EnableAdminServer
import org.springframework.boot.autoconfigure.SpringBootApplication

/**
 * @author lix wang
 */
@EnableAdminServer
@SpringBootApplication
class DemoAdminServer : BaseSpringApplication("demo-admin-server")

fun main() {
    DemoAdminServer().start()
}