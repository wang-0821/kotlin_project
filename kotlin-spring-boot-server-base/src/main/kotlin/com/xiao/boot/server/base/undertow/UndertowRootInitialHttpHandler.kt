package com.xiao.boot.server.base.undertow

import com.xiao.boot.server.base.servlet.CoroutineServerArgs
import io.undertow.server.HttpHandler
import io.undertow.server.HttpServerExchange
import io.undertow.util.AttachmentKey
import kotlinx.coroutines.launch
import java.util.concurrent.Executor

/**
 * @author lix wang
 */
class UndertowRootInitialHttpHandler(
    private val httpHandler: HttpHandler,
    private val coroutineServerArgs: CoroutineServerArgs
) : HttpHandler {
    private val coroutineExecutor = Executor { runnable ->
        coroutineServerArgs.coroutineScope!!.launch {
            runnable.run()
        }
    }

    override fun handleRequest(exchange: HttpServerExchange) {
        // Use global executor instead of undertow taskPool.
        exchange.dispatchExecutor = coroutineExecutor
        // set exchange attachment
        exchange.putAttachment(
            UNDERTOW_SERVLET_ATTACHMENT,
            UndertowExchangeAttachment(
                System.currentTimeMillis()
            )
        )
        httpHandler.handleRequest(exchange)
    }

    companion object {
        val UNDERTOW_SERVLET_ATTACHMENT: AttachmentKey<UndertowExchangeAttachment> =
            AttachmentKey.create(UndertowExchangeAttachment::class.java)
    }
}