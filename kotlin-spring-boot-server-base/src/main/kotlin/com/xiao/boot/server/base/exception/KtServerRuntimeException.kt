package com.xiao.boot.server.base.exception

import kotlin.RuntimeException

/**
 *
 * @author lix wang
 */
class KtServerRuntimeException : RuntimeException {
    var errorCode: String? = null
    var statusCode: Int? = null

    constructor() : super()
    constructor(message: String?) : super(message)
    constructor(cause: Throwable?) : super(cause)
    constructor(ex: RuntimeException) : this(ex.message, ex.cause)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
}