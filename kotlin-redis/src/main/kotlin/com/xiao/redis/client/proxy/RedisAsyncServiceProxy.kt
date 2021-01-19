package com.xiao.redis.client.proxy

import com.xiao.base.ExecutionHelper
import com.xiao.base.util.ProxyUtils
import io.lettuce.core.RedisClient
import java.lang.reflect.Method

/**
 *
 * @author lix wang
 */
class RedisAsyncServiceProxy(
    redisClient: RedisClient
) : BaseRedisProxy(redisClient) {
    override fun invoke(proxy: Any?, method: Method, args: Array<Any?>?): Any? {
        val connection = getConnection()
        return ExecutionHelper.retryableExec {
            ProxyUtils.invoke(connection.async(), method, args)
        }
    }
}