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
class KtMapperProxy<T>(private val mapper: T) : InvocationHandler {
    override fun invoke(proxy: Any, method: Method, args: Array<Any?>?): Any {
        return method.invoke(mapper, args)
    }

    private val clazz = KtMapperProxy::class.java

//    private fun execute(method: Method, args: Array<Any?>?): Any {
//        try {
//            val startTime = System.currentTimeMillis()
//            val result = method.invoke(mapper, args)
//            log.info("Mapper ${clazz.simpleName}.${method.name} consume ${System.currentTimeMillis() - startTime} ms.")
//            return result
//        } catch (e: Exception) {
//            throw e
//        }
//    }

    private fun execute(method: Method, args: Array<Any?>?, retryTimes: Int): Any {
        var exception: Exception? = null
        for (i in 0..retryTimes) {
            try {
                val startTime = System.currentTimeMillis()
                val result = method.invoke(mapper, args)
                log.info("Mapper ${clazz.simpleName}.${method.name} consume ${System.currentTimeMillis() - startTime} ms, " +
                        "total times: ${i + 1}, retry rimes $i.")
                return result
            } catch (e : Exception) {
                if (i <= retryTimes) {
                    exception = e
                }
            }
        }
        log.error("Mapper ${clazz.simpleName}.${method.name} failed.", exception)
        throw exception!!
    }

    @KtLogger(LoggerType.MAPPER)
    companion object : Logging()
}