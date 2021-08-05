package com.xiao.boot.server.undertow.interceptor

import io.undertow.server.HttpServerExchange

/**
 * Effect on undertow execution period.
 *
 * @author lix wang
 */
interface UndertowInterceptor {
    fun beforeHandle(exchange: HttpServerExchange) = Unit

    fun afterCompletion(exchange: HttpServerExchange) = Unit
}