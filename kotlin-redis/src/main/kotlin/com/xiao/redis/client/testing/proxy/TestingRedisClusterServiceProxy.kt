package com.xiao.redis.client.testing.proxy

import com.xiao.base.lock.SpinLock
import com.xiao.base.util.ProxyUtils
import com.xiao.redis.client.testing.TestingRedisClusterService
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method

/**
 * Use spin lock avoid context switch.
 *
 * @author lix wang
 */
class TestingRedisClusterServiceProxy : InvocationHandler {
    private val redisService = TestingRedisClusterService()
    private val lock = SpinLock()

    override fun invoke(proxy: Any?, method: Method, args: Array<Any?>?): Any? {
        return lock.use {
            ProxyUtils.invoke(redisService, method, args)
        }
    }
}