package com.xiao.boot.redis.config

import com.xiao.boot.redis.annotation.KtSpringRedis
import com.xiao.boot.redis.annotation.RedisClientMode
import com.xiao.boot.redis.client.BaseRedis
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component

/**
 *
 * @author lix wang
 */
@Lazy
@Component
@KtSpringRedis(
    name = DemoClusterRedis.NAME,
    mode = RedisClientMode.CLUSTER
)
class DemoClusterRedis : BaseRedis(*CLUSTER_URIS) {
    companion object {
        const val NAME = "demo"
        const val CLUSTER_SERVICE_NAME = "${NAME}RedisClusterService"
        const val CLUSTER_ASYNC_SERVICE_NAME = "${NAME}RedisClusterAsyncService"
        val CLUSTER_URIS = arrayOf(
            "redis://localhost:6381",
            "redis://localhost:6382",
            "redis://localhost:6383",
            "redis://localhost:6384",
            "redis://localhost:6385",
            "redis://localhost:6386"
        )
    }
}