package com.xiao.boot.server.base.exception

import com.xiao.base.logging.Logging
import com.xiao.boot.base.env.EnvInfoProvider
import com.xiao.boot.server.base.request.RequestContainer
import com.xiao.boot.server.base.request.RequestInfo
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import javax.servlet.http.HttpServletResponse

/**
 *
 * @author lix wang
 */
@RestControllerAdvice
class GlobalRestControllerAdvice(
    private val envInfoProvider: EnvInfoProvider
) {
    private val requestInfo = RequestContainer.getRequestValue(RequestInfo.KEY)

    // unexpected exceptions
    @ExceptionHandler(value = [Exception::class, Error::class])
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleException(
        response: HttpServletResponse,
        throwable: Throwable
    ): KtExceptionResponse {
        log.error("${throwable.message}", throwable)
        return buildExceptionResponse(null, throwable.message)
    }

    // expected exceptions
    @ExceptionHandler(value = [KtServerException::class])
    fun handleCustomException(
        response: HttpServletResponse,
        ex: KtServerException
    ): KtExceptionResponse {
        log.warn("${ex.message}", ex)
        ex.statusCode
            ?.let {
                response.status = it
            }
            ?: run {
                response.status = HttpStatus.INTERNAL_SERVER_ERROR.value()
            }
        return buildExceptionResponse(ex.errorCode, ex.message)
    }

    // expected runtime exceptions
    @ExceptionHandler(value = [KtServerRuntimeException::class])
    fun handleCustomRuntimeException(
        response: HttpServletResponse,
        ex: KtServerRuntimeException
    ): KtExceptionResponse {
        log.info("${ex.message}", ex)
        ex.statusCode
            ?.let {
                response.status = it
            }
            ?: run {
                response.status = HttpStatus.BAD_REQUEST.value()
            }
        return buildExceptionResponse(ex.errorCode, ex.message)
    }

    private fun buildExceptionResponse(errorCode: String?, message: String?): KtExceptionResponse {
        return KtExceptionResponse()
            .apply {
                this.errorCode = errorCode ?: "server.exception"
                this.message = message
                this.server = envInfoProvider.serverName()
                this.uuid = requestInfo?.getRequestUuid()
            }
    }

    companion object : Logging()
}