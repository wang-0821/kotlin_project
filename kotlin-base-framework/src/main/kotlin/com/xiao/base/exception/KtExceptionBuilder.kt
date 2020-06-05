package com.xiao.base.exception

/**
 *
 * @author lix wang
 */
open class KtExceptionBuilder: Exception {
    constructor()
    constructor(cause: Throwable): super(cause) {
        this.cause = cause
    }

    var statusCode: Int? = null
    var errorCode: String? = null
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

    override fun toString(): String {
        return "${javaClass.simpleName}(" +
                "statusCode=${statusCode ?: (cause as? KtException)?.statusCode?.toString().orEmpty()}, " +
                "errorCode='${errorCode ?: (cause as? KtException)?.errorCode.orEmpty()}', " +
                "message=${message ?: (cause as? KtException)?.message.orEmpty()})"
    }
}