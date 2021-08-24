package com.xiao.redis.client.testing

import io.lettuce.core.RedisFuture

/**
 *
 * @author lix wang
 */
class TestingRedisClusterAsyncService : AbstractTestingRedisClusterAsyncService() {
    private val delegate = TestingRedisAsyncService()

    override fun get(key: String?): RedisFuture<String> {
        return delegate.get(key)
    }

    override fun set(key: String?, value: String?): RedisFuture<String> {
        return delegate.set(key, value)
    }

    override fun setex(key: String?, seconds: Long, value: String?): RedisFuture<String> {
        return delegate.setex(key, seconds, value)
    }

    override fun expire(key: String?, seconds: Long): RedisFuture<Boolean> {
        return delegate.expire(key, seconds)
    }

    override fun del(vararg keys: String?): RedisFuture<Long> {
        return delegate.del(*keys)
    }
}