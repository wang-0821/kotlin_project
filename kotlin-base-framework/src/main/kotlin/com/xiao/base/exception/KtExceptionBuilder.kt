package com.xiao.base.exception

/**
 *
 * @author lix wang
 */
open class KtExceptionBuilder: Exception {
    constructor()
    constructor(cause: Throwable): super(cause)

    var statusCode: Int = 0
    var errorCode: String = ""
    override var message: String? = null
    override var cause: Throwable? = null

    fun statusCode(statusCode: Int): KtExceptionBuilder {
        this.statusCode = statusCode
        return this
    }

    fun errorCode(errorCode: String): KtExceptionBuilder {
        this.errorCode = errorCode
        return this
    }

    fun message(message: String?): KtExceptionBuilder {
        this.message = message
        return this
    }

    fun cause(cause: Throwable?): KtExceptionBuilder {
        this.cause = cause
        return this
    }
}