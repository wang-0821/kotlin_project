package xiao.redis.client.testing

import xiao.base.util.ProxyUtils
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method

/**
 *
 * @author lix wang
 */
class TestingRedisAsyncServiceProxy : InvocationHandler {
    private val testingRedisAsyncService = TestingRedisAsyncService()

    override fun invoke(proxy: Any?, method: Method, args: Array<Any?>?): Any? {
        synchronized(this) {
            return ProxyUtils.invoke(testingRedisAsyncService, method, args)
        }
    }
}