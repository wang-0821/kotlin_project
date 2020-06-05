package com.xiao.base.exception

/**
 *
 * @author lix wang
 */
open class KtException : KtExceptionBuilder {
    constructor(): super()
    constructor(cause: Throwable): super(cause)

    fun toRuntimeException(): KtRuntimeException {
        return KtRuntimeException(this)
    }
}