package com.xiao.boot.server.undertow.handler

import com.xiao.boot.server.undertow.interceptor.UndertowInterceptor

/**
 *
 * @author lix wang
 */
class UndertowExchangeAttachment {
    var requestStartMills: Long? = null
    var executeStartMills: Long? = null
    var requestEndMills: Long? = null
    var interceptors: List<UndertowInterceptor> = listOf()
}