package com.xiao.redis.client

import com.xiao.base.testing.KtTestBase
import kotlinx.coroutines.runBlocking
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
        clusterCommand.del(KEY)
        clusterCommand.set(KEY, VALUE)
        Assertions.assertEquals(clusterCommand.get(KEY), VALUE)
    }

    @Test
    fun `test redis cluster async commands`() {
        val clusterAsyncCommands = RedisHelper.getRedisClusterAsyncService(CLUSTER_REDIS_URLS)
        clusterAsyncCommands.del(KEY).get()
        clusterAsyncCommands.set(KEY, VALUE).get()
        Assertions.assertEquals(clusterAsyncCommands.get(KEY).get(), VALUE)
    }

    @Test
    fun `test redis cluster coroutine commands`() {
        runBlocking {
            val clusterAsyncCommands = RedisHelper.getRedisClusterAsyncService(CLUSTER_REDIS_URLS)
            clusterAsyncCommands.del(KEY).suspend().awaitNanos()
            clusterAsyncCommands.set(KEY, VALUE).suspend().awaitNanos()
            Assertions.assertEquals(clusterAsyncCommands.get(KEY).suspend().awaitNanos(), VALUE)
        }
    }

    companion object {
        private const val KEY = "hello"
        private const val VALUE = "world"
        private val CLUSTER_REDIS_URLS = setOf(
            "redis://localhost:6381",
            "redis://localhost:6382",
            "redis://localhost:6383",
            "redis://localhost:6384",
            "redis://localhost:6385",
            "redis://localhost:6386"
        )
    }
}