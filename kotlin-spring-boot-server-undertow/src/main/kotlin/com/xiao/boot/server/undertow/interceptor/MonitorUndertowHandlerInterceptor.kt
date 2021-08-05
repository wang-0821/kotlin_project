package com.xiao.boot.server.undertow.interceptor

import com.xiao.base.logging.Logging
import com.xiao.boot.server.undertow.utils.UndertowUtils
import io.undertow.server.HttpServerExchange
import io.undertow.servlet.handlers.ServletRequestContext
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest

/**
 *
 * @author lix wang
 */
@Component
class MonitorUndertowHandlerInterceptor : UndertowInterceptor {
    override fun beforeHandle(exchange: HttpServerExchange) {
        exchange.getAttachment(UndertowUtils.UNDERTOW_SERVLET_ATTACHMENT)
            .executeStartMills = System.currentTimeMillis()
    }

    override fun afterCompletion(exchange: HttpServerExchange) {
        val currentMills = System.currentTimeMillis()
        val attachment = exchange.getAttachment(UndertowUtils.UNDERTOW_SERVLET_ATTACHMENT)
        attachment.requestEndMills = currentMills
        val httpServletRequest = exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY)
            .servletRequest as HttpServletRequest
        val httpMethod = httpServletRequest.method
        val uri = exchange.requestURI
        log.info(
            "$httpMethod $uri, total consume: ${currentMills - attachment.requestStartMills!!} ms, " +
                "running: ${currentMills - attachment.executeStartMills!!} ms."
        )
    }

    companion object : Logging()
}