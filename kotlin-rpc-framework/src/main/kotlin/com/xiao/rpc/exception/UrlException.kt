package com.xiao.rpc.exception

import com.xiao.base.exception.HttpStatus
import com.xiao.base.exception.KtException

/**
 *
 * @author lix wang
 */
object UrlException {
    fun invalidFormat() = KtException().statusCode(HttpStatus.SC_FORBIDDEN).message("Invalid url format")
    fun noProtocol() = KtException().statusCode(HttpStatus.SC_FORBIDDEN).message("Url has no protocol")
    fun unIdentifiedHost() = KtException().statusCode(HttpStatus.SC_FORBIDDEN).message("Identify host failed")
}