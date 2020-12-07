package com.xiao.base.util

import java.lang.reflect.Method

/**
 *
 * @author lix wang
 */
object ProxyUtils {
    fun invoke(proxy: Any, method: Method, args: Array<Any?>?): Any {
        val argArrays = args ?: arrayOf()
        return method.invoke(proxy, *argArrays)
    }
}