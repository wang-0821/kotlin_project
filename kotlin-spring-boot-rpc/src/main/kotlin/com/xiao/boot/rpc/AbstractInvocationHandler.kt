package com.xiao.boot.rpc

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method

abstract class AbstractInvocationHandler<out T>(
    private val target: Any,
    val clazz: Class<out T>,
    private val nextHandler: AbstractInvocationHandler<T>? = null
): InvocationHandler {
    override fun invoke(proxy: Any, method: Method, args: Array<Any?>?): Any? {
        return nextHandler?.invoke(proxy, method, args) ?: doInvoke(proxy, method, args)
    }

    abstract fun doInvoke(proxy: Any, method: Method, args: Array<Any?>?): Any?
}