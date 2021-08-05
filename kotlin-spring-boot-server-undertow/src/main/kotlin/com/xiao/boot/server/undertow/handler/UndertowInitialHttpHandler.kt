package com.xiao.boot.server.undertow.handler

import com.xiao.boot.server.undertow.interceptor.UndertowInterceptor
import com.xiao.boot.server.undertow.utils.UndertowUtils.UNDERTOW_SERVLET_ATTACHMENT
import io.undertow.server.HttpHandler
import io.undertow.server.HttpServerExchange
import org.springframework.context.ApplicationContext

/**
 *
 * @author lix wang
 */
open class UndertowInitialHttpHandler(
    applicationContext: ApplicationContext,
    private val httpHandler: HttpHandler
) : HttpHandler {
    private val undertowHandlers = applicationContext.getBeansOfType(UndertowInterceptor::class.java).values.toList()

    override fun handleRequest(exchange: HttpServerExchange) {
        // set exchange attachment
        exchange.putAttachment(
            UNDERTOW_SERVLET_ATTACHMENT,
            UndertowExchangeAttachment()
                .apply {
                    requestStartMills = System.currentTimeMillis()
                    interceptors = undertowHandlers
                }
        )
        httpHandler.handleRequest(exchange)
    }
}