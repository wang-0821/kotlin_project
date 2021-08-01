package com.xiao.boot.server.base.undertow

import kotlinx.coroutines.CoroutineScope
import java.util.concurrent.ExecutorService

/**
 *
 * @author lix wang
 */
data class UndertowExechangeAttachment(
    val requestStartMills: Long = -1,
    val executorService: ExecutorService? = null,
    val coroutineScope: CoroutineScope? = null
)