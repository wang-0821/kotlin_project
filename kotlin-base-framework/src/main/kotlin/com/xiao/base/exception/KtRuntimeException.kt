package com.xiao.base.exception

/**
 *
 * @author lix wang
 */
class KtRuntimeException : RuntimeException {
    constructor(cause: Throwable): super(cause)
    constructor(message: String): super(message)
}