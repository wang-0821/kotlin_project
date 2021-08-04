package com.xiao.boot.server.base.servlet

import kotlinx.coroutines.CoroutineScope
import org.springframework.stereotype.Component
import java.util.concurrent.ExecutorService

/**
 *
 * @author lix wang
 */
@Component
class KtServerArgs {
    var enableCoroutineDispatcher: Boolean = false
    var coroutineScope: CoroutineScope? = null
    var executorService: ExecutorService? = null
}