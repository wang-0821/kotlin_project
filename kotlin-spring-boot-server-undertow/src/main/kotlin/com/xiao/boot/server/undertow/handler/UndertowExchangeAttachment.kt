package com.xiao.boot.server.undertow.handler

import com.xiao.boot.server.undertow.interceptor.UndertowInterceptor

/**
 *
 * @author lix wang
 */
class UndertowExchangeAttachment {
    var interceptors: List<UndertowInterceptor> = listOf()
}