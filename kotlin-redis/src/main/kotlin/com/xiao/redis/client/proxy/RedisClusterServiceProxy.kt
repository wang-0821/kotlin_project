package com.xiao.redis.client.proxy

import com.xiao.base.logging.Logging
import com.xiao.base.util.ProxyUtils
import io.lettuce.core.cluster.RedisClusterClient
import java.lang.reflect.Method

/**
 *
 * @author lix wang
 */
class RedisClusterServiceProxy(redisClient: RedisClusterClient) : BaseRedisClusterProxy(redisClient) {
    override fun invoke(proxy: Any?, method: Method, args: Array<Any?>?): Any? {
        try {
            return ProxyUtils.invoke(getConnection().sync(), method, args)
        } catch (e: Exception) {
            log.error("Redis cluster sync method: ${method.name} failed, ${e.message}.", e)
            throw e
        }
    }

    companion object : Logging()
}