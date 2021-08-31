package com.xiao.boot.admin.client

import com.xiao.boot.base.BaseSpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

/**
 *
 * @author lix wang
 */
@SpringBootApplication
class DemoAdminClient : BaseSpringApplication("demo-admin-client")

fun main() {
    DemoAdminClient().start()
}