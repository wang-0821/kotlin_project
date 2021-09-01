package com.xiao.boot.server.undertow.handler

import com.xiao.base.thread.CoroutineThreadLocal
import com.xiao.boot.server.base.mvc.KtServerArgs
import com.xiao.boot.server.base.request.CoroutineLogContext
import com.xiao.boot.server.undertow.request.UndertowRequestInfo
import io.undertow.server.HttpHandler
import io.undertow.server.HttpServerExchange
import kotlinx.coroutines.launch
import org.springframework.context.ApplicationContext
import java.util.UUID
import java.util.concurrent.Executor
import kotlin.coroutines.CoroutineContext

/**
 * @author lix wang
 */
class UndertowCoroutineInitialHttpHandler(
    applicationContext: ApplicationContext,
    httpHandler: HttpHandler,
    private val ktServerArgs: KtServerArgs
) : UndertowInitialHttpHandler(applicationContext, httpHandler) {
    // TODO improve handleRequest by replace exchange dispatchTask.
    override fun handleRequest(exchange: HttpServerExchange) {
        // Use global executor instead of undertow taskPool.
        val requestUuid = UUID.randomUUID().toString()
        prepareAttachment(exchange, requestUuid)
        exchange.dispatchExecutor = getExecutor(exchange, requestUuid)
        httpHandler.handleRequest(exchange)
    }

    private fun getExecutor(exchange: HttpServerExchange, requestUuid: String): Executor {
        return Executor { runnable ->
            ktServerArgs.coroutineScope!!.launch(
                createCoroutineContext(requestUuid)
            ) {
                executeTask(runnable, exchange)
            }
        }
    }

    private fun createCoroutineContext(requestUuid: String): CoroutineContext {
        val requestInfo = UndertowRequestInfo()
            .apply {
                requestStartMills = System.currentTimeMillis()
                this.requestUuid = requestUuid
            }
        return CoroutineThreadLocal(
            threadLocal,
            requestInfo
        ).apply {
            requestInfo.threadContextElement = this
        } + CoroutineLogContext(requestInfo.requestUuid!!)
    }
}