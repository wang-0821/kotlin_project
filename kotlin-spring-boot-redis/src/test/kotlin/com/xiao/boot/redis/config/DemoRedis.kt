package com.xiao.boot.redis.config

import com.xiao.boot.redis.annotation.KtSpringRedis
import com.xiao.boot.redis.client.BaseRedis
import org.springframework.stereotype.Component

/**
 *
 * @author lix wang
 */
@Component
@KtSpringRedis(name = DemoRedis.NAME)
class DemoRedis : BaseRedis(REDIS_URI) {
    companion object {
        const val NAME = "demo"
        const val SERVICE_NAME = "${NAME}RedisService"
        const val ASYNC_SERVICE_NAME = "${NAME}RedisAsyncService"
        const val REDIS_URI = "redis://localhost:6379"
    }
}