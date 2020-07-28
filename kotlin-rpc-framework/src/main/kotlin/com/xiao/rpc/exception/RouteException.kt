package com.xiao.rpc.exception

import com.xiao.base.exception.HttpStatus
import com.xiao.base.exception.KtException

/**
 *
 * @author lix wang
 */
sealed class RouteException(cause: Throwable) : KtException(cause) {
    class RouteNotFoundException(cause: Throwable) : RouteException(cause)

    companion object {
        fun noAvailableRoutes(message: String? = null): RouteNotFoundException {
            return RouteNotFoundException(
                KtException()
                    .statusCode(HttpStatus.SC_NOT_FOUND)
                    .message("No available route $message")
            )
        }
    }
}