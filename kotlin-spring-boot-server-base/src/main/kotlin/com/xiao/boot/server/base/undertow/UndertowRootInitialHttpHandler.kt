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
        exchange.putAttachment(
            UNDERTOW_SERVLET_ATTACHMENT,
            UndertowExechangeAttachment(
                System.currentTimeMillis(),
                KtThreadPool.globalPool,
                KtThreadPool.globalCoroutineContext
            )
        )
        KtThreadPool.globalPool.execute {
            httpHandler.handleRequest(exchange)
        }
    }

    companion object {
        val UNDERTOW_SERVLET_ATTACHMENT: AttachmentKey<UndertowExechangeAttachment> =
            AttachmentKey.create(UndertowExechangeAttachment::class.java)
    }
}