package com.xiao.redis.client.proxy

import com.xiao.base.logging.KtLogger
import com.xiao.base.logging.LoggerType
import com.xiao.base.logging.Logging
import com.xiao.base.util.ProxyUtils
import io.lettuce.core.cluster.RedisClusterClient
import java.lang.reflect.Method

/**
 *
 * @author lix wang
 */
class RedisClusterAsyncServiceProxy(redisClient: RedisClusterClient) : BaseRedisClusterProxy(redisClient) {
    override fun invoke(proxy: Any?, method: Method, args: Array<Any?>?): Any? {
        try {
            return ProxyUtils.invoke(getConnection().async(), method, args)
        } catch (e: Exception) {
            log.error("Redis cluster async method: ${method.name} failed, ${e.message}.", e)
            throw e
        }
    }

    @KtLogger(LoggerType.REDIS)
    companion object : Logging()
}