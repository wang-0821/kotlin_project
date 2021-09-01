package com.xiao.boot.admin.client

import com.xiao.boot.base.BaseSpringApplication
import com.xiao.boot.server.base.annotations.CoroutineSpringBootApplication

/**
 *
 * @author lix wang
 */
@CoroutineSpringBootApplication
class DemoAdminClient : BaseSpringApplication("demo-admin-client")

fun main() {
    DemoAdminClient().start()
}