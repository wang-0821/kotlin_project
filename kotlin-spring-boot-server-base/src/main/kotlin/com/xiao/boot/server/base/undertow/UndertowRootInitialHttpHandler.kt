package com.xiao.boot.server.base.undertow

import com.xiao.boot.base.thread.KtThreadPool
import com.xiao.boot.server.base.undertow.UndertowThreadPool.UNDERTOW_THREAD_POOL_ATTACHMENT
import io.undertow.server.HttpHandler
import io.undertow.server.HttpServerExchange

/**
 * @author lix wang
 */
class UndertowRootInitialHttpHandler(
    private val httpHandler: HttpHandler
) : HttpHandler {
    override fun handleRequest(exchange: HttpServerExchange) {
        exchange.putAttachment(UNDERTOW_THREAD_POOL_ATTACHMENT, KtThreadPool.workerPool)
        httpHandler.handleRequest(exchange)
    }
}