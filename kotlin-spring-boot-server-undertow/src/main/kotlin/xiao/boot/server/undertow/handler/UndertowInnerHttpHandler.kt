package xiao.boot.server.undertow.handler

import io.undertow.server.HttpHandler
import io.undertow.server.HttpServerExchange

/**
 *
 * @author lix wang
 */
class UndertowInnerHttpHandler(
    private val httpHandler: HttpHandler
) : HttpHandler {
    override fun handleRequest(exchange: HttpServerExchange) {
        val attachment = exchange.getAttachment(UndertowExchangeAttachment.UNDERTOW_SERVLET_ATTACHMENT)
        attachment.interceptors
            .forEach {
                it.beforeHandle(exchange)
            }
        httpHandler.handleRequest(exchange)
        attachment.interceptors
            .forEach {
                it.afterCompletion(exchange)
            }
    }
}