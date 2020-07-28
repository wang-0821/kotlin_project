package com.xiao.rpc.exception

import com.xiao.base.exception.HttpStatus
import com.xiao.base.exception.KtException

/**
 *
 * @author lix wang
 */
object ChainException {
    fun executeOutOfBound() = KtException()
        .statusCode(HttpStatus.SC_BAD_REQUEST)
        .message("Execute chain out of bounds")

    fun noResponseError(message: String) = KtException()
        .statusCode(HttpStatus.SC_CONFLICT)
        .message("Execute request but have no response returned $message")
}