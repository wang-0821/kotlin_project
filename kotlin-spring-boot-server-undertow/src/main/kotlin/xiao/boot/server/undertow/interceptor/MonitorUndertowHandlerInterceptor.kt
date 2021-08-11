package xiao.boot.server.undertow.interceptor

import io.undertow.server.HttpServerExchange
import io.undertow.servlet.handlers.ServletRequestContext
import org.springframework.stereotype.Component
import xiao.base.logging.Logging
import xiao.boot.server.base.mvc.RequestContainer
import xiao.boot.server.base.mvc.RequestInfo
import javax.servlet.http.HttpServletRequest

/**
 *
 * @author lix wang
 */
@Component
class MonitorUndertowHandlerInterceptor : UndertowInterceptor {
    override fun beforeHandle(exchange: HttpServerExchange) {
        RequestContainer.getRequestValue(RequestInfo.KEY)
            ?.setExecuteStartMills(System.currentTimeMillis())
    }

    override fun afterCompletion(exchange: HttpServerExchange) {
        RequestContainer.getRequestValue(RequestInfo.KEY)
            ?.let {
                val currentMills = System.currentTimeMillis()
                val httpServletRequest = exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY)
                    .servletRequest as HttpServletRequest
                val httpMethod = httpServletRequest.method
                val uri = exchange.requestURI
                it.setRequestEndMills(currentMills)
                log.info(
                    "$httpMethod $uri, status: ${it.getThrowable()?.let { "FAILED" } ?: "SUCCEED"}, " +
                        "total consume: ${currentMills - it.getRequestStartMills()!!} ms, " +
                        "running: ${currentMills - it.getExecuteStartMills()!!} ms."
                )
            }
    }

    companion object : Logging()
}