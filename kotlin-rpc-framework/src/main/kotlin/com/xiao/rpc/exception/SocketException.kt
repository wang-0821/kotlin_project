package com.xiao.rpc.exception

import com.xiao.base.exception.HttpStatus
import com.xiao.base.exception.KtException

/**
 *
 * @author lix wang
 */
sealed class SocketException(cause: Throwable) : KtException(cause) {
    class ConnectTimeoutExceptionConnection(cause: Throwable) : SocketException(cause)

    class ReadTimeoutExceptionConnection(cause: Throwable) : SocketException(cause)

    class WriteTimeoutExceptionConnection(cause: Throwable) : SocketException(cause)

    class StateException(cause: Throwable): SocketException(cause)

    companion object {
        fun connectTimeout(message: String? = null): ConnectTimeoutExceptionConnection {
            return ConnectTimeoutExceptionConnection(
                KtException()
                    .statusCode(HttpStatus.SC_REQUEST_TIMEOUT)
                    .message("Connect timeout $message"))
        }

        fun readTimeout(message: String? = null): ReadTimeoutExceptionConnection {
            return ReadTimeoutExceptionConnection(
                KtException()
                    .statusCode(HttpStatus.SC_REQUEST_TIMEOUT)
                    .message("Read timeout. $message"))
        }

        fun writeTimeout(message: String? = null): WriteTimeoutExceptionConnection {
            return WriteTimeoutExceptionConnection(
                KtException()
                    .statusCode(HttpStatus.SC_REQUEST_TIMEOUT)
                    .message("Write timeout. $message"))
        }

        fun connectFailed(message: String? = null): StateException {
            return StateException(
                KtException()
                    .statusCode(HttpStatus.SC_FORBIDDEN)
                    .message("Socket connect failed. $message")
            )
        }
    }
}