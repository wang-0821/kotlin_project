package com.xiao.boot.mybatis.invocation

import com.xiao.base.logging.Logging
import com.xiao.base.util.ProxyUtils
import com.xiao.boot.mybatis.annotation.MapperRetry
import java.lang.reflect.Method

/**
 *
 * @author lix wang
 */
class RetryKtInvocation(
    sourceClass: Class<*>
) : KtInvocation() {
    private val methodExecuteTimes: Map<Method, Int>

    init {
        methodExecuteTimes = parseMethodRetryTimes(sourceClass)
    }

    override fun invoke(obj: Any, method: Method, args: Array<Any?>?): Any? {
        val executeTimes = methodExecuteTimes[method] ?: 1
        return if (executeTimes > 1) {
            doRetryInvoke(executeTimes, obj, method, args)
        } else {
            doExecuteMethod(obj, method, args)
        }
    }

    private fun doRetryInvoke(executeTimes: Int, obj: Any, method: Method, args: Array<Any?>?): Any? {
        var ex: Exception? = null
        (1..executeTimes).forEach { times ->
            try {
                return doExecuteMethod(obj, method, args)
            } catch (e: Exception) {
                ex = e
                log.warn("execute: ${method.name} failed,  times: $times.")
            }
        }
        throw IllegalStateException("execute: ${method.name} failed, total times: $executeTimes.", ex)
    }

    private fun parseMethodRetryTimes(sourceClass: Class<*>): Map<Method, Int> {
        val methodExecuteTimes = HashMap<Method, Int>()
        val baseExecuteTimes = sourceClass.getAnnotation(MapperRetry::class.java)?.times ?: 1
        sourceClass.methods
            .forEach { method ->
                methodExecuteTimes[method] = method.getAnnotation(MapperRetry::class.java)
                    ?.let {
                        it.times + 1
                    } ?: baseExecuteTimes
            }
        return methodExecuteTimes
    }

    private fun doExecuteMethod(obj: Any, method: Method, args: Array<Any?>?): Any? {
        return invocation?.invoke(obj, method, args) ?: ProxyUtils.invoke(obj, method, args)
    }

    companion object : Logging()
}