package com.xiao.boot.server.undertow.handler

import com.xiao.base.thread.CoroutineThreadLocal
import com.xiao.boot.server.base.mvc.KtServerArgs
import com.xiao.boot.server.undertow.common.UndertowRequestInfo
import io.undertow.server.HttpHandler
import io.undertow.server.HttpServerExchange
import kotlinx.coroutines.launch
import org.springframework.context.ApplicationContext
import java.util.UUID
import java.util.concurrent.Executor

/**
 * @author lix wang
 */
class UndertowCoroutineInitialHttpHandler(
    applicationContext: ApplicationContext,
    httpHandler: HttpHandler,
    private val ktServerArgs: KtServerArgs
) : UndertowInitialHttpHandler(applicationContext, httpHandler) {
    private val defaultExecutor = Executor { runnable ->
        ktServerArgs.coroutineScope!!.launch(
            createCoroutineThreadLocal()
        ) {
            runnable.run()
        }
    }

    // TODO improve handleRequest by replace exchange dispatchTask.
    override fun handleRequest(exchange: HttpServerExchange) {
        // Use global executor instead of undertow taskPool.
        prepareAttachment(exchange)
        exchange.dispatchExecutor = defaultExecutor
        httpHandler.handleRequest(exchange)
    }

    private fun createCoroutineThreadLocal(): CoroutineThreadLocal<UndertowRequestInfo> {
        val requestInfo = UndertowRequestInfo()
            .apply {
                val uuid = UUID.randomUUID().toString()
                requestStartMills = System.currentTimeMillis()
                requestUuid = uuid
            }
        return CoroutineThreadLocal(
            threadLocal,
            requestInfo
        ).apply {
            requestInfo.threadContextElement = this
        }
    }
}