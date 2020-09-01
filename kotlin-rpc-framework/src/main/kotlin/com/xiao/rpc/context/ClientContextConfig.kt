package com.xiao.rpc.context

import java.util.concurrent.TimeUnit

/**
 *
 * @author lix wang
 */
data class ClientContextConfig(val singleCorePoolSize: Int, val idleTimeout: Int, val timeUnit: TimeUnit)