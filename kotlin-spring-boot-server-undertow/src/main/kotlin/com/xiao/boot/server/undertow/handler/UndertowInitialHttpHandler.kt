package com.xiao.boot.server.undertow.handler

import com.xiao.base.thread.KtFastThreadLocal
import com.xiao.boot.server.base.mvc.RequestContainer
import com.xiao.boot.server.base.mvc.RequestInfo
import com.xiao.boot.server.undertow.common.UndertowRequestInfo
import com.xiao.boot.server.undertow.common.UndertowThreadLocalRequestInfo
import com.xiao.boot.server.undertow.interceptor.UndertowInterceptor
import com.xiao.boot.server.undertow.utils.UndertowUtils.UNDERTOW_SERVLET_ATTACHMENT
import io.undertow.server.HttpHandler
import io.undertow.server.HttpServerExchange
import org.springframework.context.ApplicationContext
import java.util.UUID

/**
 *
 * @author lix wang
 */
open class UndertowInitialHttpHandler(
    applicationContext: ApplicationContext,
    val httpHandler: HttpHandler
) : HttpHandler {
    private val undertowHandlers = applicationContext.getBeansOfType(UndertowInterceptor::class.java)
        .values.toList()

    override fun handleRequest(exchange: HttpServerExchange) {
        prepareAttachment(exchange)
        threadLocal.set(
            UndertowRequestInfo()
                .apply {
                    requestStartMills = System.currentTimeMillis()
                    requestUuid = UUID.randomUUID().toString()
                }
        )
        try {
            httpHandler.handleRequest(exchange)
        } finally {
            threadLocal.reset()
        }
    }

    protected fun prepareAttachment(exchange: HttpServerExchange) {
        exchange.putAttachment(
            UNDERTOW_SERVLET_ATTACHMENT,
            UndertowExchangeAttachment()
                .apply {
                    interceptors = undertowHandlers
                }
        )
    }

    companion object {
        val threadLocal = KtFastThreadLocal<UndertowRequestInfo>()
            .apply {
                RequestContainer.register(
                    RequestInfo.KEY,
                    UndertowThreadLocalRequestInfo(this)
                )
            }
    }
}