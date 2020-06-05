package com.xiao.rpc.exception

import com.xiao.base.exception.HttpStatus
import com.xiao.base.exception.KtException

/**
 *
 * @author lix wang
 */
object ExecuteException {
    fun executeOutOfBound() = KtException()
        .statusCode(HttpStatus.SC_BAD_REQUEST)
        .message("Execute chain out of bounds")

    fun noResponseError(message: String) = KtException()
        .statusCode(HttpStatus.SC_CONFLICT)
        .message("Execute request but have no response returned $message")

    fun notSupportedProtocol() = KtException()
        .statusCode(HttpStatus.SC_BAD_REQUEST)
        .message("No protocol or not support.")
}

open class RouteException(cause: Throwable) : KtException(cause) {
    class NoAvailableRouteException(cause: Throwable) : RouteException(cause)

    companion object {
        fun noAvailableRoutes(message: String? = null): NoAvailableRouteException {
            return NoAvailableRouteException(KtException()
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .message("No available route $message")
            )
        }
    }
}

open class DnsException(cause: Throwable) : KtException(cause) {
    class DnsDomainResolveException(cause: Throwable) : DnsException(cause)

    companion object {
        fun dnsDomainResolveException(message: String? = null): DnsDomainResolveException {
            return DnsDomainResolveException(KtException().
                statusCode(HttpStatus.SC_NOT_FOUND)
                .message("Dns domain resolve failed $message")
            )
        }
    }
}

open class ConnectionException(cause: Throwable) : KtException(cause) {
    class ConnectTimeoutExceptionConnection(cause: Throwable) : ConnectionException(cause)

    class ReadTimeoutExceptionConnection(cause: Throwable) : ConnectionException(cause)

    class WriteTimeoutExceptionConnection(cause: Throwable) : ConnectionException(cause)

    companion object {
        fun connectTimeout(message: String? = null): ConnectTimeoutExceptionConnection {
            return ConnectTimeoutExceptionConnection(KtException()
                .statusCode(HttpStatus.SC_REQUEST_TIMEOUT)
                .message("Connect timeout $message"))
        }

        fun readTimeout(message: String? = null): ReadTimeoutExceptionConnection {
            return ReadTimeoutExceptionConnection(KtException()
                .statusCode(HttpStatus.SC_REQUEST_TIMEOUT)
                .message("Read timeout $message"))
        }

        fun writeTimeout(message: String? = null): WriteTimeoutExceptionConnection {
            return WriteTimeoutExceptionConnection(KtException()
                .statusCode(HttpStatus.SC_REQUEST_TIMEOUT)
                .message("Write timeout $message"))
        }
    }
}