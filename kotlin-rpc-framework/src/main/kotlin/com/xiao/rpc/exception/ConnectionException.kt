package com.xiao.rpc.exception

import com.xiao.base.exception.HttpStatus
import com.xiao.base.exception.KtException

/**
 *
 * @author lix wang
 */
class ConnectionException(cause: Throwable) : KtException(cause) {
    companion object {
        fun noAvailableConnection(message: String = ""): ConnectionException {
            return ConnectionException(
                KtException().
                    statusCode(HttpStatus.SC_FORBIDDEN)
                    .message("No available connection. $message")
            )
        }
    }
}