package com.xiao.redis.client.testing

import com.xiao.base.CommonConstants
import org.joda.time.DateTime

/**
 *
 * @author lix wang
 */
class TestingRedisService : AbstractTestingRedisService() {
    private val map = mutableMapOf<String?, String?>()
    private val expireMap = mutableMapOf<String?, DateTime>()

    override fun get(key: String?): String? {
        val value = map[key]
        return if (isKeyExpired(key)) {
            null
        } else {
            value
        }
    }

    override fun set(key: String?, value: String?): String {
        map[key] = value
        return CommonConstants.STATUS_OK
    }

    override fun setex(key: String?, seconds: Long, value: String?): String {
        map[key] = value
        expireMap[key] = DateTime.now().plusSeconds(seconds.toInt())
        return CommonConstants.STATUS_OK
    }

    override fun expire(key: String?, seconds: Long): Boolean {
        (expireMap[key] ?: DateTime.now()).plusSeconds(seconds.toInt())
        return true
    }

    override fun del(vararg keys: String?): Long {
        var result = 0L
        keys.forEach {
            if (map[it] != null) {
                map.remove(it)
                expireMap.remove(it)
                result++
            }
        }
        return result
    }

    private fun isKeyExpired(key: String?): Boolean {
        val expired = expireMap[key] != null && expireMap[key]!!.isBeforeNow
        if (expired) {
            del(key)
        }
        return expired
    }
}