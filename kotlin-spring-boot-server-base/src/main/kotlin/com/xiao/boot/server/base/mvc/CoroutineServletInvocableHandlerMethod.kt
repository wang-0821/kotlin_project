package com.xiao.boot.server.base.mvc

import com.xiao.base.logging.Logging
import kotlinx.coroutines.runBlocking
import org.springframework.core.KotlinDetector
import org.springframework.util.ReflectionUtils
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import kotlin.reflect.full.callSuspend
import kotlin.reflect.jvm.kotlinFunction

/**
 * @author lix wang
 */
class CoroutineServletInvocableHandlerMethod : ServletInvocableHandlerMethod {
    internal var ktServerArgs: KtServerArgs? = null

    constructor(handler: Any, method: Method) : super(handler, method)
    constructor(handlerMethod: HandlerMethod) : super(handlerMethod)

    // TODO use global suspend instead of runBlocking.
    @Throws(Exception::class)
    override fun doInvoke(vararg args: Any?): Any? {
        val method = bridgedMethod
        ReflectionUtils.makeAccessible(method)
        return try {
            if (KotlinDetector.isSuspendingFunction(method)) {
                val useCoroutineCall = ktServerArgs?.let {
                    it.enableCoroutineDispatcher && it.coroutineScope != null
                } ?: false

                if (useCoroutineCall) {
                    runBlocking(ktServerArgs!!.coroutineScope!!.coroutineContext) {
                        method.kotlinFunction!!.callSuspend(bean, *args)
                    }
                } else {
                    log.warn("Method: ${method.name} is suspend, but don't have valid coroutine scope.")
                    method.invoke(bean, *args)
                }
            } else {
                method.invoke(bean, *args)
            }
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