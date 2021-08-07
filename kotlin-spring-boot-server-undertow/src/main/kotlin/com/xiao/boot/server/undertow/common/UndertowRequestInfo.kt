package com.xiao.boot.server.undertow.common

import kotlinx.coroutines.ThreadContextElement

/**
 * @author lix wang
 */
data class UndertowRequestInfo(
    var requestStartMills: Long? = null,
    var executeStartMills: Long? = null,
    var requestEndMills: Long? = null,
    var throwable: Throwable? = null,
    var requestUuid: String? = null,
    var threadContextElement: ThreadContextElement<*>? = null
)