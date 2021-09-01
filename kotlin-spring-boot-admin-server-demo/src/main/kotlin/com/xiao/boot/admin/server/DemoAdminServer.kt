package com.xiao.boot.admin.server

import com.xiao.boot.base.BaseSpringApplication
import com.xiao.boot.server.base.annotations.CoroutineSpringBootApplication
import de.codecentric.boot.admin.server.config.EnableAdminServer

/**
 * @author lix wang
 */
@EnableAdminServer
@CoroutineSpringBootApplication
class DemoAdminServer : BaseSpringApplication("demo-admin-server")

fun main() {
    DemoAdminServer().start()
}