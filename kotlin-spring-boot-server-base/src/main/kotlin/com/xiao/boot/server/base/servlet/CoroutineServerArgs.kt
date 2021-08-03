package com.xiao.boot.server.base.servlet

import kotlinx.coroutines.CoroutineScope
import java.util.concurrent.ExecutorService

/**
 *
 * @author lix wang
 */
class CoroutineServerArgs {
    var enableGlobalDispatcher: Boolean = false
        internal set
    var coroutineScope: CoroutineScope? = null
        internal set
    var executorService: ExecutorService? = null
        internal set
}