package com.xiao.boot.mybatis.proxy

import com.xiao.base.logging.Logging
import java.lang.reflect.Method
import java.util.concurrent.Callable

/**
 *
 * @author lix wang
 */
class RetryMapperInvocation(
    private val method: Method,
    private val retryTimes: Int
) : MapperInvocation {
    var callable: Callable<Any?>? = null

    override fun proceed(invocation: MapperInvocation?): Any? {
        var ex: Exception? = null
        (0..retryTimes).forEach { times ->
            try {
                return callable?.call()
            } catch (e: Exception) {
                ex = e
                log.warn("execute method: ${method.name} failed ${times + 1} times.")
            }
        }
        throw IllegalStateException("execute method: ${method.name} failed.", ex)
    }

    companion object : Logging()
}