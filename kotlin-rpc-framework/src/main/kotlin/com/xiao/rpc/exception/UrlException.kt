package com.xiao.rpc.exception

import com.xiao.base.exception.HttpStatus
import com.xiao.base.exception.KtException

/**
 *
 * @author lix wang
 */
object UrlException {
    fun noScheme() = invalid("Url has no scheme.")
    fun noHost() = invalid("Url has no host.")
    fun invalidParamFormat() = invalid("Url has invalid param format.")

    private fun invalid(message: String) = KtException().statusCode(HttpStatus.SC_FORBIDDEN).message(message)
}