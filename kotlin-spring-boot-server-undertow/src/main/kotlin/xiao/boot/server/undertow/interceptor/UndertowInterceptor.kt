package xiao.boot.server.undertow.interceptor

import io.undertow.server.HttpServerExchange
import org.springframework.core.Ordered

/**
 * Effect on undertow execution period.
 *
 * @author lix wang
 */
interface UndertowInterceptor : Ordered {
    fun beforeHandle(exchange: HttpServerExchange) = Unit

    fun afterCompletion(exchange: HttpServerExchange) = Unit

    override fun getOrder(): Int {
        return Ordered.LOWEST_PRECEDENCE
    }
}