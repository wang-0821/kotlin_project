package com.xiao.boot.mybatis.invocation

import java.lang.reflect.Method

/**
 *
 * @author lix wang
 */
abstract class KtInvocation {
    var invocation: KtInvocation? = null

    abstract fun invoke(obj: Any, method: Method, args: Array<Any?>?): Any?
}