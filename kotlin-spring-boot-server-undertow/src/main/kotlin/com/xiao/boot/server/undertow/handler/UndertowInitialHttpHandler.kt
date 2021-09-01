package com.xiao.boot.server.undertow.handler

import com.xiao.base.thread.KtFastThreadLocal
import com.xiao.boot.server.base.request.RequestContainer
import com.xiao.boot.server.base.request.RequestInfo
import com.xiao.boot.server.base.request.RequestInfo.Companion.KEY_LOG_X_REQUEST_UUID
import com.xiao.boot.server.undertow.handler.UndertowExchangeAttachment.Companion.UNDERTOW_SERVLET_ATTACHMENT
import com.xiao.boot.server.undertow.interceptor.UndertowInterceptor
import com.xiao.boot.server.undertow.request.UndertowRequestInfo
import com.xiao.boot.server.undertow.request.UndertowThreadLocalRequestInfo
import io.undertow.server.HttpHandler
import io.undertow.server.HttpServerExchange
import org.apache.logging.log4j.ThreadContext
import org.springframework.context.ApplicationContext
import java.util.UUID
import java.util.concurrent.Executor

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
        val requestUuid = UUID.randomUUID().toString()
        prepareAttachment(exchange, requestUuid)
        exchange.dispatchExecutor = getExecutor(exchange, requestUuid)
        httpHandler.handleRequest(exchange)
    }

    protected fun prepareAttachment(exchange: HttpServerExchange, requestUuid: String) {
        exchange.putAttachment(
            UNDERTOW_SERVLET_ATTACHMENT,
            UndertowExchangeAttachment()
                .apply {
                    interceptors = undertowHandlers
                    this.requestUuid = requestUuid
                }
        )
    }

    protected fun executeTask(runnable: Runnable, exchange: HttpServerExchange) {
        undertowHandlers.forEach { it.beforeHandle(exchange) }
        runnable.run()
        undertowHandlers.forEach { it.afterCompletion(exchange) }
    }

    private fun getExecutor(exchange: HttpServerExchange, requestUuid: String): Executor {
        return Executor { runnable ->
            val requestInfo = UndertowRequestInfo()
                .apply {
                    requestStartMills = System.currentTimeMillis()
                    this.requestUuid = requestUuid
                }
            val executor = exchange.dispatchExecutor ?: exchange.connection.worker
            executor.execute {
                threadLocal.set(requestInfo)
                ThreadContext.put(KEY_LOG_X_REQUEST_UUID, requestInfo.requestUuid)
                try {
                    executeTask(runnable, exchange)
                } finally {
                    threadLocal.set(null)
                    ThreadContext.remove(KEY_LOG_X_REQUEST_UUID)
                }
            }
        }
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