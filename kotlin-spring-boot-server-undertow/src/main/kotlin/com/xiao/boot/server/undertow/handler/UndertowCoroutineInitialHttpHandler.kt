package com.xiao.boot.server.undertow.handler

import com.xiao.boot.server.base.mvc.KtServerArgs
import io.undertow.server.HttpHandler
import io.undertow.server.HttpServerExchange
import kotlinx.coroutines.launch
import org.springframework.context.ApplicationContext
import java.util.concurrent.Executor

/**
 * @author lix wang
 */
class UndertowCoroutineInitialHttpHandler(
    applicationContext: ApplicationContext,
    httpHandler: HttpHandler,
    private val ktServerArgs: KtServerArgs
) : UndertowInitialHttpHandler(applicationContext, httpHandler) {
    private val coroutineExecutor = Executor { runnable ->
        ktServerArgs.coroutineScope!!.launch {
            runnable.run()
        }
    }

    override fun handleRequest(exchange: HttpServerExchange) {
        // Use global executor instead of undertow taskPool.
        exchange.dispatchExecutor = coroutineExecutor
        super.handleRequest(exchange)
    }
}