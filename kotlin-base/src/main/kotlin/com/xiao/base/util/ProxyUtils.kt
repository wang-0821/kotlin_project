package com.xiao.base.util

import java.lang.reflect.Method

/**
 *
 * @author lix wang
 */
object ProxyUtils {
    /**
     * Set method accessible as true, skip security check, to speed up.
     */
    @JvmStatic
    fun invoke(proxy: Any, method: Method, args: Array<Any?>?): Any? {
        if (!method.isAccessible) {
            method.isAccessible = true
        }
        val argArrays = args ?: arrayOf()
        return method.invoke(proxy, *argArrays)
    }
}