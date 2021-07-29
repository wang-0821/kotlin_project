package com.xiao.boot.server.base.undertow

import java.util.concurrent.ExecutorService
import kotlin.coroutines.CoroutineContext

/**
 *
 * @author lix wang
 */
data class UndertowExechangeAttachment(
    val requestStartMills: Long = -1,
    val executorService: ExecutorService? = null,
    val coroutineContext: CoroutineContext? = null,
)