package com.xiao.boot.server.base.undertow

import com.xiao.boot.base.thread.KtThreadPool
import io.undertow.server.HttpHandler
import io.undertow.server.HttpServerExchange
import io.undertow.util.AttachmentKey

/**
 * @author lix wang
 */
class UndertowRootInitialHttpHandler(
    private val httpHandler: HttpHandler
) : HttpHandler {
    override fun handleRequest(exchange: HttpServerExchange) {
        // Use global executor instead of undertow taskPool.
        exchange.dispatchExecutor = KtThreadPool.globalPool
        // set exchange attachment
        exchange.putAttachment(
            UNDERTOW_SERVLET_ATTACHMENT,
            UndertowExechangeAttachment(
                System.currentTimeMillis(),
                KtThreadPool.globalPool,
                KtThreadPool.globalCoroutineScope
            )
        )
        httpHandler.handleRequest(exchange)
    }

    companion object {
        val UNDERTOW_SERVLET_ATTACHMENT: AttachmentKey<UndertowExechangeAttachment> =
            AttachmentKey.create(UndertowExechangeAttachment::class.java)
    }
}