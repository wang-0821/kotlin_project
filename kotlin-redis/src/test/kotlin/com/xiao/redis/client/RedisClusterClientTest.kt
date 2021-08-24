package com.xiao.redis.client

import com.xiao.base.testing.KtTestBase
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

/**
 * TODO config redis cluster in github actions.
 *
 * @author lix wang
 */
@Disabled
class RedisClusterClientTest : KtTestBase() {
    @Test
    fun `test redis cluster sync commands`() {
        val clusterCommand = RedisHelper.getRedisClusterService(CLUSTER_REDIS_URLS)
        clusterCommand.set(KEY, VALUE)
        Assertions.assertEquals(clusterCommand.get(KEY), VALUE)
    }

    companion object {
        private const val KEY = "hello"
        private const val VALUE = "world"
        private val CLUSTER_REDIS_URLS = setOf(
            "redis://localhost:6380",
            "redis://localhost:6381",
            "redis://localhost:6382",
            "redis://localhost:6383",
            "redis://localhost:6384",
            "redis://localhost:6385"
        )
    }
}