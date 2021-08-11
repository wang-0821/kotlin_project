package xiao.redis.client.proxy

import io.lettuce.core.RedisClient
import io.lettuce.core.api.StatefulRedisConnection
import java.lang.reflect.InvocationHandler
import java.time.Duration

/**
 *
 * @author lix wang
 */
abstract class BaseRedisProxy(private val redisClient: RedisClient) : InvocationHandler {
    @Volatile
    private var connection: StatefulRedisConnection<String, String>? = null

    fun getConnection(): StatefulRedisConnection<String, String> {
        return if (isConnectionValid()) {
            connection!!
        } else {
            val oldConnection = connection
            synchronized(redisClient) {
                if (isConnectionValid() && oldConnection != connection) {
                    return connection!!
                }
                redisClient.defaultTimeout = REDIS_TIMEOUT
                connection = redisClient.connect()
                connection!!
            }
        }
    }

    private fun isConnectionValid(): Boolean {
        return connection != null && connection!!.isOpen
    }

    companion object {
        private val REDIS_TIMEOUT = Duration.ofSeconds(5)
    }
}