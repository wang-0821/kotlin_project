package com.xiao.rpc.exception

import com.xiao.base.exception.HttpStatus
import com.xiao.base.exception.KtException

/**
 *
 * @author lix wang
 */
object ExecuteException {
    fun executeOutOfBound() = KtException().statusCode(HttpStatus.SC_BAD_REQUEST).message("Execute chain out of bounds")
}