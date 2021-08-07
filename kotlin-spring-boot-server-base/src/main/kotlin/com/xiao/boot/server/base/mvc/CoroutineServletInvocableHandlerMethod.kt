package com.xiao.boot.server.base.mvc

import com.xiao.base.logging.Logging
import kotlinx.coroutines.runBlocking
import org.springframework.core.KotlinDetector
import org.springframework.core.MethodParameter
import org.springframework.util.ReflectionUtils
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.HandlerMethod
import org.springframework.web.method.support.ModelAndViewContainer
import org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod
import java.lang.reflect.InvocationTargetException
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.reflect.full.callSuspend
import kotlin.reflect.jvm.kotlinFunction

/**
 * @author lix wang
 */
class CoroutineServletInvocableHandlerMethod(
    handlerMethod: HandlerMethod
) : ServletInvocableHandlerMethod(handlerMethod) {
    internal var ktServerArgs: KtServerArgs? = null
    private val isSuspendMethod = KotlinDetector.isSuspendingFunction(bridgedMethod)

    override fun invokeForRequest(
        request: NativeWebRequest,
        mavContainer: ModelAndViewContainer?,
        vararg providedArgs: Any?
    ): Any? {
        return if (isSuspendMethod) {
            // TODO use coroutine whole request process.
            runBlocking(getCoroutineContext()) {
                val args = getMethodArgumentValues(request, mavContainer, *providedArgs)
                invokeMethodSuspend(*args)
            }
        } else {
            val args = getMethodArgumentValues(request, mavContainer, *providedArgs)
            invokeMethod(*args)
        }
    }

    // TODO replace with one coroutine, otherwise each coroutine need set threadContextElement once.
    private fun getCoroutineContext(): CoroutineContext {
        var coroutineContext = ktServerArgs?.coroutineScope?.coroutineContext ?: EmptyCoroutineContext
        RequestContainer.getRequestValue(RequestInfo.KEY)
            ?.let { requestInfo ->
                if (requestInfo is CoroutineRequestInfo) {
                    requestInfo.getThreadContextElement()
                        ?.let { threadContextElement ->
                            coroutineContext += threadContextElement
                        }
                }
            }
        return coroutineContext
    }

    override fun getMethodParameters(): Array<MethodParameter> {
        return if (isSuspendMethod) {
            super.getMethodParameters().copyOfRange(0, super.getMethodParameters().size - 1)
        } else {
            super.getMethodParameters()
        }
    }

    private fun invokeMethod(vararg args: Any?): Any? {
        val method = bridgedMethod
        ReflectionUtils.makeAccessible(method)
        return try {
            method.invoke(bean, *args)
        } catch (ex: IllegalArgumentException) {
            assertTargetBean(method, bean, args)
            throw IllegalStateException(formatInvokeError(ex.message ?: "Illegal argument", args), ex)
        } catch (ex: InvocationTargetException) {
            // Unwrap for HandlerExceptionResolvers ...
            when (val targetException = ex.targetException) {
                is RuntimeException -> {
                    throw targetException
                }
                is Error -> {
                    throw targetException
                }
                is Exception -> {
                    throw targetException
                }
                else -> {
                    throw IllegalStateException(formatInvokeError("Invocation failure", args), targetException)
                }
            }
        }
    }

    @Throws(Exception::class)
    private suspend fun invokeMethodSuspend(vararg args: Any?): Any? {
        val method = bridgedMethod
        ReflectionUtils.makeAccessible(method)
        return try {
            method.kotlinFunction!!.callSuspend(bean, *args)
        } catch (ex: IllegalArgumentException) {
            assertTargetBean(method, bean, args)
            throw IllegalStateException(formatInvokeError(ex.message ?: "Illegal argument", args), ex)
        } catch (ex: InvocationTargetException) {
            // Unwrap for HandlerExceptionResolvers ...
            when (val targetException = ex.targetException) {
                is RuntimeException -> {
                    throw targetException
                }
                is Error -> {
                    throw targetException
                }
                is Exception -> {
                    throw targetException
                }
                else -> {
                    throw IllegalStateException(formatInvokeError("Invocation failure", args), targetException)
                }
            }
        }
    }

    companion object : Logging()
}