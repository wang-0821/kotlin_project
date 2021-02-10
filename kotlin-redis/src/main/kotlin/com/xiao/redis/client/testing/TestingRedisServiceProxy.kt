package com.xiao.redis.client.testing

import com.xiao.base.util.ProxyUtils
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method

/**
 *
 * @author lix wang
 */
class TestingRedisServiceProxy : InvocationHandler {
    private val redisService = TestingRedisService()

    override fun invoke(proxy: Any?, method: Method, args: Array<Any?>?): Any? {
        synchronized(this) {
            return ProxyUtils.invoke(redisService, method, args)
        }
    }
}