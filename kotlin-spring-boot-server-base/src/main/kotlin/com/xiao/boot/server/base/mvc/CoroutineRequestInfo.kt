package com.xiao.boot.server.base.mvc

import kotlinx.coroutines.ThreadContextElement

/**
 * @author lix wang
 */
interface CoroutineRequestInfo : RequestInfo {
    fun getThreadContextElement(): ThreadContextElement<*>?
}