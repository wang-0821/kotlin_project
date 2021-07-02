package com.xiao.boot.mybatis.proxy

import com.xiao.boot.mybatis.annotation.MapperRetry
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method

/**
 *
 * @author lix wang
 */
class KtSpringMapperProxy<T>(
    private val sourceClass: Class<T>,
    private val mapper: T
) : InvocationHandler {
    override fun invoke(proxy: Any, method: Method, args: Array<Any?>?): Any? {
        if (sourceClass.isAnnotationPresent(MapperRetry::class.java)) {

        }
    }
}