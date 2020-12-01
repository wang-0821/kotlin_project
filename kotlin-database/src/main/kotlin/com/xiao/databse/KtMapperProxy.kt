package com.xiao.databse

import com.xiao.base.logging.KtLogger
import com.xiao.base.logging.LoggerType
import com.xiao.base.logging.Logging
import com.xiao.databse.annotation.KtMapperRetry
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method

/**
 *
 * @author lix wang
 */
class KtMapperProxy<T>(private val clazz: Class<T>, private val mapper: T) : InvocationHandler {
    override fun invoke(proxy: Any, method: Method, args: Array<Any?>?): Any {
        val mapperRetry = method.getAnnotation(KtMapperRetry::class.java)
            ?: clazz.getAnnotation(KtMapperRetry::class.java)
        val executeTimes = mapperRetry?.times ?: 0 + 1
        return execute(method, args, executeTimes)
    }

    private fun execute(method: Method, args: Array<Any?>?, times: Int): Any {
        var exception: Exception? = null
        for (i in 1..times) {
            try {
                val startTime = System.currentTimeMillis()
                val result = method.invoke(mapper, args)
                log.info("Mapper ${clazz.simpleName}.${method.name} "
                    + "consume ${System.currentTimeMillis() - startTime} ms, "
                    + "total times: $i, retry rimes ${i - 1}.")
                return result
            } catch (e : Exception) {
                exception = e
            }
        }
        log.error("Mapper ${clazz.simpleName}.${method.name} failed.", exception)
        throw exception!!
    }

    @KtLogger(LoggerType.MAPPER)
    companion object : Logging()
}