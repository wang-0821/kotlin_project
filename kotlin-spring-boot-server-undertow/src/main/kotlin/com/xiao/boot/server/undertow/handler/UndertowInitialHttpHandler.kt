package com.xiao.boot.server.undertow.handler

import com.xiao.base.thread.KtFastThreadLocal
import com.xiao.boot.server.base.request.RequestContainer
import com.xiao.boot.server.base.request.RequestInfo
import com.xiao.boot.server.base.request.RequestInfo.Companion.KEY_LOG_X_REQUEST_UUID
import com.xiao.boot.server.undertow.request.UndertowRequestInfo
import com.xiao.boot.server.undertow.request.UndertowThreadLocalRequestInfo
import com.xiao.boot.server.undertow.handler.UndertowExchangeAttachment.Companion.UNDERTOW_SERVLET_ATTACHMENT
import com.xiao.boot.server.undertow.interceptor.UndertowInterceptor
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
        prepareAttachment(exchange)
        exchange.dispatchExecutor = getExecutor(exchange)
        httpHandler.handleRequest(exchange)
    }

    private fun getExecutor(exchange: HttpServerExchange): Executor {
        return Executor { runnable ->
            val executor = exchange.dispatchExecutor ?: exchange.connection.worker
            executor.execute {
                executeDispatchTask(runnable)
            }
        }
    }

    private fun executeDispatchTask(runnable: Runnable) {
        val requestInfo = UndertowRequestInfo()
            .apply {
                requestStartMills = System.currentTimeMillis()
                requestUuid = UUID.randomUUID().toString()
            }
        threadLocal.set(requestInfo)
        ThreadContext.put(KEY_LOG_X_REQUEST_UUID, requestInfo.requestUuid)
        try {
            runnable.run()
        } finally {
            threadLocal.set(null)
            ThreadContext.remove(KEY_LOG_X_REQUEST_UUID)
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