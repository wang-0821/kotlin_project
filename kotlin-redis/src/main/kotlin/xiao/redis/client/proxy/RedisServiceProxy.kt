package xiao.redis.client.proxy

import io.lettuce.core.RedisClient
import xiao.base.logging.KtLogger
import xiao.base.logging.LoggerType
import xiao.base.logging.Logging
import xiao.base.util.ProxyUtils
import java.lang.reflect.Method

/**
 *
 * @author lix wang
 */
class RedisServiceProxy(
    redisClient: RedisClient
) : BaseRedisProxy(redisClient) {
    override fun invoke(proxy: Any?, method: Method, args: Array<Any?>?): Any? {
        try {
            return ProxyUtils.invoke(getConnection().sync(), method, args)
        } catch (e: Exception) {
            log.error("Redis sync method: ${method.name} failed, ${e.message}.", e)
            throw e
        }
    }

    @KtLogger(LoggerType.REDIS)
    companion object : Logging()
}