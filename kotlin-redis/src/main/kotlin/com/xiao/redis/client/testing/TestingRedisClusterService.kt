package com.xiao.redis.client.testing

/**
 *
 * @author lix wang
 */
class TestingRedisClusterService : AbstractTestingRedisClusterService() {
    private val delegate = TestingRedisService()

    override fun get(key: String?): String? {
        return delegate.get(key)
    }

    override fun set(key: String?, value: String?): String {
        return delegate.set(key, value)
    }

    override fun setex(key: String?, seconds: Long, value: String?): String {
        return delegate.setex(key, seconds, value)
    }

    override fun expire(key: String?, seconds: Long): Boolean {
        return delegate.expire(key, seconds)
    }

    override fun del(vararg keys: String?): Long {
        return delegate.del(*keys)
    }
}